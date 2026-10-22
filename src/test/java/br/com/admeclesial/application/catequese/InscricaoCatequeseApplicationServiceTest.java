package br.com.admeclesial.application.catequese;

import br.com.admeclesial.domain.catequese.*;
import br.com.admeclesial.domain.pessoa.Pessoa;
import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.infrastructure.persistence.catequese.InscricaoCatequeseRepository;
import br.com.admeclesial.infrastructure.persistence.catequese.TurmaCatequeseRepository;
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
class InscricaoCatequeseApplicationServiceTest {

    @Mock private InscricaoCatequeseRepository repository;
    @Mock private TurmaCatequeseRepository turmaRepository;
    @Mock private PessoaRepository pessoaRepository;
    @InjectMocks private InscricaoCatequeseApplicationService service;

    private UUID turmaUuid;
    private UUID pessoaUuid;
    private TurmaCatequese turma;
    private EtapaCatequese etapa;
    private Catequese catequese;
    private Pessoa pessoa;
    private OrganizacaoEclesial organizacao;

    @BeforeEach
    void setUp() {
        turmaUuid = UUID.randomUUID();
        pessoaUuid = UUID.randomUUID();
        turma = mock(TurmaCatequese.class);
        etapa = mock(EtapaCatequese.class);
        catequese = mock(Catequese.class);
        pessoa = mock(Pessoa.class);
        organizacao = mock(OrganizacaoEclesial.class);

        when(turma.getUuid()).thenReturn(turmaUuid);
        when(turma.getOrganizacaoEclesial()).thenReturn(organizacao);
        when(organizacao.getUuid()).thenReturn(UUID.randomUUID());
        when(turma.getId()).thenReturn(50L);
        when(turma.getStatus()).thenReturn(StatusCatequese.PLANEJADA);
        when(turma.getEtapaCatequese()).thenReturn(etapa);
        when(etapa.getId()).thenReturn(60L);
        when(etapa.getNome()).thenReturn("Primeira Etapa");
        when(etapa.getCatequese()).thenReturn(catequese);
        when(catequese.getId()).thenReturn(70L);
        when(pessoa.getUuid()).thenReturn(pessoaUuid);
        when(pessoa.getId()).thenReturn(80L);
        when(pessoa.getNome()).thenReturn("Maria");
        when(pessoa.isAtivo()).thenReturn(true);

        when(turmaRepository.findByUuid(turmaUuid)).thenReturn(Optional.of(turma));
        when(pessoaRepository.findByUuid(pessoaUuid)).thenReturn(Optional.of(pessoa));
        when(repository.existsInscricaoAtivaNaEtapa(anyLong(), anyLong(), anyLong(), any())).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void deveCriarInscricaoAtivaPorPadrao() {
        var response = service.criar(new InscricaoCatequeseRequest(turmaUuid, pessoaUuid, null, null, ""));

        assertEquals(StatusInscricaoCatequese.ATIVA, response.status());
        assertEquals(LocalDate.now(), response.dataInscricao());
        verify(repository).save(any());
    }

    @Test
    void deveRejeitarPessoaInativa() {
        when(pessoa.isAtivo()).thenReturn(false);

        var ex = assertThrows(CatequeseBusinessException.class,
                () -> service.criar(request(StatusInscricaoCatequese.ATIVA)));

        assertEquals("Não é possível inscrever uma pessoa inativa.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarInscricaoEmTurmaEncerrada() {
        when(turma.getStatus()).thenReturn(StatusCatequese.ENCERRADA);

        var ex = assertThrows(CatequeseBusinessException.class,
                () -> service.criar(request(StatusInscricaoCatequese.ATIVA)));

        assertEquals("Não é possível realizar inscrição em turma encerrada ou cancelada.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarInscricaoDuplicadaNaMesmaEtapa() {
        when(repository.existsInscricaoAtivaNaEtapa(80L, 70L, 60L, StatusInscricaoCatequese.ATIVA)).thenReturn(true);

        var ex = assertThrows(CatequeseBusinessException.class,
                () -> service.criar(request(StatusInscricaoCatequese.ATIVA)));

        assertEquals("A pessoa já possui inscrição ativa nesta etapa da catequese.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void devePermitirNovaInscricaoQuandoNaoExisteAtivaNaEtapa() {
        var response = service.criar(request(StatusInscricaoCatequese.CANCELADA));

        assertEquals(StatusInscricaoCatequese.CANCELADA, response.status());
        verify(repository).save(any());
    }

    private InscricaoCatequeseRequest request(StatusInscricaoCatequese status) {
        return new InscricaoCatequeseRequest(turmaUuid, pessoaUuid, LocalDate.of(2026, 2, 10), status, null);
    }
}
