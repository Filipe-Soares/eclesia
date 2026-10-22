package br.com.admeclesial.application.organizacao;

import br.com.admeclesial.domain.organizacao.*;
import br.com.admeclesial.domain.pessoa.Pessoa;
import br.com.admeclesial.infrastructure.persistence.organizacao.FuncaoEclesialRepository;
import br.com.admeclesial.infrastructure.persistence.organizacao.VinculoFuncaoRepository;
import br.com.admeclesial.infrastructure.persistence.organizacao.VinculoPessoaOrganizacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VinculoFuncaoApplicationServiceTest {

    @Mock private VinculoFuncaoRepository repository;
    @Mock private VinculoPessoaOrganizacaoRepository vinculoRepository;
    @Mock private FuncaoEclesialRepository funcaoRepository;

    @InjectMocks private VinculoFuncaoApplicationService service;

    private UUID vinculoUuid;
    private UUID funcaoUuid;
    private VinculoPessoaOrganizacaoEclesial vinculo;
    private FuncaoEclesial funcao;
    private Pessoa pessoa;
    private OrganizacaoEclesial organizacao;

    @BeforeEach
    void setUp() {
        vinculoUuid = UUID.randomUUID();
        funcaoUuid = UUID.randomUUID();
        vinculo = mock(VinculoPessoaOrganizacaoEclesial.class);
        funcao = mock(FuncaoEclesial.class);
        pessoa = mock(Pessoa.class);
        organizacao = mock(OrganizacaoEclesial.class);

        when(vinculo.getUuid()).thenReturn(vinculoUuid);
        when(vinculo.getId()).thenReturn(100L);
        when(vinculo.getPessoa()).thenReturn(pessoa);
        when(vinculo.getOrganizacaoEclesial()).thenReturn(organizacao);
        when(vinculo.getDataInicio()).thenReturn(LocalDate.of(2026, 1, 1));
        when(vinculo.getDataFim()).thenReturn(null);
        when(vinculo.getStatus()).thenReturn(StatusVinculoPessoaOrganizacao.ATIVO);

        when(pessoa.getUuid()).thenReturn(UUID.randomUUID());
        when(pessoa.getNome()).thenReturn("João");
        when(organizacao.getUuid()).thenReturn(UUID.randomUUID());
        when(organizacao.getNome()).thenReturn("Paróquia Teste");
        when(organizacao.getId()).thenReturn(200L);

        when(funcao.getUuid()).thenReturn(funcaoUuid);
        when(funcao.getId()).thenReturn(300L);
        when(funcao.getCodigo()).thenReturn("CATEQUISTA");
        when(funcao.getNome()).thenReturn("Catequista");
        when(funcao.isAtiva()).thenReturn(true);
        when(funcao.isExclusivoNaOrganizacao()).thenReturn(false);

        when(vinculoRepository.findByUuid(vinculoUuid)).thenReturn(Optional.of(vinculo));
        when(funcaoRepository.findByUuid(funcaoUuid)).thenReturn(Optional.of(funcao));
        when(repository.existsConflitoFuncaoNoVinculo(anyLong(), anyLong(), any(), any(), isNull())).thenReturn(false);
        when(repository.existsPrincipalConflitante(anyLong(), any(), any(), isNull())).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void devePermitirVariasFuncoesSimultaneasNaMesmaOrganizacao() {
        var funcao2 = mock(FuncaoEclesial.class);
        when(funcao2.getUuid()).thenReturn(UUID.randomUUID());
        when(funcao2.getId()).thenReturn(301L);
        when(funcao2.getCodigo()).thenReturn("SECRETARIO");
        when(funcao2.getNome()).thenReturn("Secretário");
        when(funcao2.isAtiva()).thenReturn(true);
        when(funcao2.isExclusivoNaOrganizacao()).thenReturn(false);

        var primeira = service.criar(request(funcaoUuid, LocalDate.of(2026, 1, 1), null, false));
        assertEquals("CATEQUISTA", primeira.funcaoCodigo());

        when(funcaoRepository.findByUuid(funcao2.getUuid())).thenReturn(Optional.of(funcao2));
        var segundaRequest = request(funcao2.getUuid(), LocalDate.of(2026, 1, 1), null, false);
        var segunda = service.criar(segundaRequest);

        assertEquals("SECRETARIO", segunda.funcaoCodigo());
        verify(repository, times(2)).save(any());
    }

    @Test
    void deveRejeitarFuncaoInativa() {
        when(funcao.isAtiva()).thenReturn(false);

        var ex = assertThrows(VinculoBusinessException.class,
                () -> service.criar(request(funcaoUuid, LocalDate.of(2026, 1, 1), null, false)));

        assertEquals("Não é possível atribuir uma função eclesial inativa.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarFuncaoEmVinculoNaoAtivo() {
        when(vinculo.getStatus()).thenReturn(StatusVinculoPessoaOrganizacao.SUSPENSO);

        var ex = assertThrows(VinculoBusinessException.class,
                () -> service.criar(request(funcaoUuid, LocalDate.of(2026, 1, 1), null, false)));

        assertTrue(ex.getMessage().contains("vínculo pessoa-organização"));
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarFuncaoForaDoPeriodoDoVinculo() {
        var ex = assertThrows(VinculoBusinessException.class,
                () -> service.criar(request(funcaoUuid, LocalDate.of(2025, 12, 1), null, false)));

        assertTrue(ex.getMessage().contains("período da função"));
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarMesmaFuncaoSobrepostaNoMesmoVinculo() {
        when(repository.existsConflitoFuncaoNoVinculo(eq(100L), eq(300L), any(), any(), isNull())).thenReturn(true);

        var ex = assertThrows(VinculoBusinessException.class,
                () -> service.criar(request(funcaoUuid, LocalDate.of(2026, 1, 1), null, false)));

        assertTrue(ex.getMessage().contains("esta mesma função"));
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarFuncaoExclusivaJaAtribuidaNaOrganizacao() {
        when(funcao.isExclusivoNaOrganizacao()).thenReturn(true);
        when(repository.existsConflitoFuncao(eq(200L), eq(300L), any(), any(), isNull())).thenReturn(true);

        var ex = assertThrows(VinculoBusinessException.class,
                () -> service.criar(request(funcaoUuid, LocalDate.of(2026, 1, 1), null, false)));

        assertTrue(ex.getMessage().contains("é exclusiva nesta organização"));
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarSegundaFuncaoPrincipalSobreposta() {
        when(repository.existsPrincipalConflitante(eq(100L), any(), any(), isNull())).thenReturn(true);

        var ex = assertThrows(VinculoBusinessException.class,
                () -> service.criar(request(funcaoUuid, LocalDate.of(2026, 1, 1), null, true)));

        assertTrue(ex.getMessage().contains("função principal"));
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarPeriodoDaFuncaoForaDoFimDoVinculo() {
        when(vinculo.getDataFim()).thenReturn(LocalDate.of(2026, 6, 30));

        var ex = assertThrows(VinculoBusinessException.class,
                () -> service.criar(request(funcaoUuid, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 1), false)));

        assertTrue(ex.getMessage().contains("período da função"));
        verify(repository, never()).save(any());
    }

    private VinculoFuncaoRequest request(UUID funcaoId, LocalDate inicio, LocalDate fim, boolean principal) {
        return new VinculoFuncaoRequest(vinculoUuid, funcaoId, inicio, fim, principal, "");
    }
}
