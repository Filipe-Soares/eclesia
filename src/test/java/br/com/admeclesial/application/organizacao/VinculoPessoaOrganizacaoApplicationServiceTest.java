package br.com.admeclesial.application.organizacao;

import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.domain.organizacao.StatusVinculoPessoaOrganizacao;
import br.com.admeclesial.domain.pessoa.Pessoa;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import br.com.admeclesial.infrastructure.persistence.organizacao.VinculoPessoaOrganizacaoRepository;
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
class VinculoPessoaOrganizacaoApplicationServiceTest {

    @Mock private VinculoPessoaOrganizacaoRepository repository;
    @Mock private PessoaRepository pessoaRepository;
    @Mock private OrganizacaoEclesialRepository organizacaoRepository;

    @InjectMocks private VinculoPessoaOrganizacaoApplicationService service;

    private UUID pessoaUuid;
    private UUID organizacaoUuid;
    private Pessoa pessoa;
    private OrganizacaoEclesial organizacao;

    @BeforeEach
    void setUp() {
        pessoaUuid = UUID.randomUUID();
        organizacaoUuid = UUID.randomUUID();
        pessoa = mock(Pessoa.class);
        organizacao = mock(OrganizacaoEclesial.class);
        when(pessoa.getUuid()).thenReturn(pessoaUuid);
        when(pessoa.getNome()).thenReturn("Maria");
        when(pessoa.getId()).thenReturn(10L);
        when(pessoa.isAtivo()).thenReturn(true);
        when(organizacao.getUuid()).thenReturn(organizacaoUuid);
        when(organizacao.getNome()).thenReturn("Paróquia Teste");
        when(organizacao.getId()).thenReturn(20L);
        when(organizacao.isAtivo()).thenReturn(true);
        when(pessoaRepository.findByUuid(pessoaUuid)).thenReturn(Optional.of(pessoa));
        when(organizacaoRepository.findByUuid(organizacaoUuid)).thenReturn(Optional.of(organizacao));
    }

    @Test
    void deveCriarVinculoPrincipalAtivo() {
        var request = request(LocalDate.of(2026, 1, 1), null, StatusVinculoPessoaOrganizacao.ATIVO, true);
        when(repository.existsPrincipalConflitante(eq(10L), eq(20L), any(), isNull(), isNull())).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = service.criar(request);

        assertEquals(pessoaUuid, response.pessoaUuid());
        assertEquals(organizacaoUuid, response.organizacaoUuid());
        assertTrue(response.principal());
        verify(repository).save(any());
    }

    @Test
    void devePermitirVinculoNaoPrincipalQuandoJaExistePrincipalEmOutroPeriodo() {
        var request = request(LocalDate.of(2026, 1, 1), null, StatusVinculoPessoaOrganizacao.ATIVO, false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = service.criar(request);

        assertFalse(response.principal());
        verify(repository, never()).existsPrincipalConflitante(anyLong(), anyLong(), any(), any(), any());
    }

    @Test
    void deveRejeitarPessoaInativa() {
        when(pessoa.isAtivo()).thenReturn(false);
        var request = request(LocalDate.of(2026, 1, 1), null, StatusVinculoPessoaOrganizacao.ATIVO, false);

        var ex = assertThrows(VinculoBusinessException.class, () -> service.criar(request));

        assertEquals("Não é possível criar vínculo para uma pessoa inativa.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarOrganizacaoInativa() {
        when(organizacao.isAtivo()).thenReturn(false);
        var request = request(LocalDate.of(2026, 1, 1), null, StatusVinculoPessoaOrganizacao.ATIVO, false);

        var ex = assertThrows(VinculoBusinessException.class, () -> service.criar(request));

        assertEquals("Não é possível criar vínculo com uma organização eclesial inativa.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarPeriodoInvalido() {
        var request = request(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 4, 30), StatusVinculoPessoaOrganizacao.ATIVO, false);

        var ex = assertThrows(VinculoBusinessException.class, () -> service.criar(request));

        assertEquals("Data final não pode ser anterior à data inicial.", ex.getMessage());
        verifyNoInteractions(pessoaRepository, organizacaoRepository, repository);
    }

    @Test
    void deveRejeitarSegundoVinculoPrincipalComPeriodoConflitante() {
        var request = request(LocalDate.of(2026, 1, 1), null, StatusVinculoPessoaOrganizacao.ATIVO, true);
        when(repository.existsPrincipalConflitante(eq(10L), eq(20L), any(), isNull(), isNull())).thenReturn(true);

        var ex = assertThrows(VinculoBusinessException.class, () -> service.criar(request));

        assertTrue(ex.getMessage().contains("outro vínculo principal"));
        verify(repository, never()).save(any());
    }

    @Test
    void devePermitirDoisVinculosNaoPrincipaisDaMesmaPessoaOrganizacao() {
        var request1 = request(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30), StatusVinculoPessoaOrganizacao.ATIVO, false);
        var request2 = request(LocalDate.of(2026, 7, 1), null, StatusVinculoPessoaOrganizacao.ATIVO, false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.criar(request1));
        assertDoesNotThrow(() -> service.criar(request2));
        verify(repository, times(2)).save(any());
    }

    private VinculoPessoaOrganizacaoRequest request(LocalDate inicio, LocalDate fim,
                                                      StatusVinculoPessoaOrganizacao status, boolean principal) {
        return new VinculoPessoaOrganizacaoRequest(pessoaUuid, organizacaoUuid, inicio, fim, status, principal, "");
    }
}
