package br.com.admeclesial.application.catequese;

import br.com.admeclesial.domain.catequese.*;
import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.domain.pessoa.Pessoa;
import br.com.admeclesial.infrastructure.persistence.catequese.CatequistaTurmaRepository;
import br.com.admeclesial.infrastructure.persistence.catequese.TurmaCatequeseRepository;
import br.com.admeclesial.infrastructure.persistence.organizacao.VinculoFuncaoRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.PessoaRepository;
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
class CatequistaTurmaApplicationServiceTest {

    @Mock private CatequistaTurmaRepository repository;
    @Mock private TurmaCatequeseRepository turmaRepository;
    @Mock private PessoaRepository pessoaRepository;
    @Mock private VinculoFuncaoRepository vinculoFuncaoRepository;
    @InjectMocks private CatequistaTurmaApplicationService service;

    private UUID turmaUuid;
    private UUID pessoaUuid;
    private TurmaCatequese turma;
    private Pessoa pessoa;
    private OrganizacaoEclesial organizacao;

    @BeforeEach
    void setUp() {
        turmaUuid = UUID.randomUUID();
        pessoaUuid = UUID.randomUUID();
        turma = mock(TurmaCatequese.class);
        pessoa = mock(Pessoa.class);
        organizacao = mock(OrganizacaoEclesial.class);

        when(turma.getUuid()).thenReturn(turmaUuid);
        when(turma.getId()).thenReturn(90L);
        when(turma.getOrganizacaoEclesial()).thenReturn(organizacao);
        when(organizacao.getId()).thenReturn(100L);
        when(pessoa.getUuid()).thenReturn(pessoaUuid);
        when(pessoa.getId()).thenReturn(110L);
        when(pessoa.getNome()).thenReturn("João");
        when(pessoa.isAtivo()).thenReturn(true);

        when(turmaRepository.findByUuid(turmaUuid)).thenReturn(Optional.of(turma));
        when(pessoaRepository.findByUuid(pessoaUuid)).thenReturn(Optional.of(pessoa));
        when(vinculoFuncaoRepository.pessoaPossuiFuncaoCatequista(eq(110L), eq(100L), any(), any())).thenReturn(true);
        when(repository.existsPrincipalConflitante(anyLong(), any(), any(), isNull())).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void deveAtribuirCatequistaComFuncaoCatequistaAtiva() {
        var response = service.criar(request(false, LocalDate.of(2026, 2, 1), null));

        assertEquals(pessoaUuid, response.pessoaUuid());
        assertFalse(response.principal());
        verify(repository).save(any());
    }

    @Test
    void deveRejeitarPessoaInativa() {
        when(pessoa.isAtivo()).thenReturn(false);

        var ex = assertThrows(CatequeseBusinessException.class,
                () -> service.criar(request(false, LocalDate.of(2026, 2, 1), null)));

        assertEquals("A pessoa está inativa.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarPessoaSemFuncaoCatequistaNaOrganizacao() {
        when(vinculoFuncaoRepository.pessoaPossuiFuncaoCatequista(eq(110L), eq(100L), any(), any())).thenReturn(false);

        var ex = assertThrows(CatequeseBusinessException.class,
                () -> service.criar(request(false, LocalDate.of(2026, 2, 1), null)));

        assertTrue(ex.getMessage().contains("não possui função eclesial CATEQUISTA"));
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarDoisCatequistasPrincipaisNoMesmoPeriodo() {
        when(repository.existsPrincipalConflitante(eq(90L), any(), any(), isNull())).thenReturn(true);

        var ex = assertThrows(CatequeseBusinessException.class,
                () -> service.criar(request(true, LocalDate.of(2026, 2, 1), null)));

        assertEquals("Já existe catequista principal na turma para o período informado.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void devePermitirMaisDeUmCatequistaQuandoNaoSaoPrincipais() {
        var first = service.criar(request(false, LocalDate.of(2026, 2, 1), null));
        when(repository.existsPessoaConflitante(anyLong(), anyLong(), any(), any(), isNull())).thenReturn(false);
        var second = service.criar(request(false, LocalDate.of(2026, 2, 1), null));

        assertFalse(first.principal());
        assertFalse(second.principal());
        verify(repository, times(2)).save(any());
        verify(repository, never()).existsPrincipalConflitante(anyLong(), any(), any(), any());
    }

    @Test
    void deveRejeitarPeriodoInvertido() {
        var ex = assertThrows(CatequeseBusinessException.class,
                () -> service.criar(request(false, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 9, 30))));

        assertEquals("Data final não pode ser anterior à data inicial.", ex.getMessage());
        verifyNoInteractions(turmaRepository, pessoaRepository, vinculoFuncaoRepository, repository);
    }

    private CatequistaTurmaRequest request(boolean principal, LocalDate inicio, LocalDate fim) {
        return new CatequistaTurmaRequest(turmaUuid, pessoaUuid, inicio, fim, principal, null);
    }

    @Test
    void naoDeveDuplicarMesmaPessoaNaTurma() {
        when(repository.existsPessoaConflitante(anyLong(), anyLong(), any(), any(), isNull())).thenReturn(true);
        var ex = assertThrows(CatequeseBusinessException.class, () -> service.criar(new CatequistaTurmaRequest(
                turmaUuid, pessoaUuid, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30), false, null)));
        assertTrue(ex.getMessage().contains("já está vinculada"));
    }
}
