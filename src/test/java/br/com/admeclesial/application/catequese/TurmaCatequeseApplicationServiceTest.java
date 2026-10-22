package br.com.admeclesial.application.catequese;

import br.com.admeclesial.domain.catequese.*;
import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.infrastructure.persistence.catequese.EtapaCatequeseRepository;
import br.com.admeclesial.infrastructure.persistence.catequese.TurmaCatequeseRepository;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
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
class TurmaCatequeseApplicationServiceTest {

    @Mock private TurmaCatequeseRepository repository;
    @Mock private EtapaCatequeseRepository etapaRepository;
    @Mock private OrganizacaoEclesialRepository organizacaoRepository;
    @InjectMocks private TurmaCatequeseApplicationService service;

    private UUID etapaUuid;
    private UUID organizacaoUuid;
    private EtapaCatequese etapa;
    private Catequese catequese;
    private OrganizacaoEclesial organizacao;

    @BeforeEach
    void setUp() {
        etapaUuid = UUID.randomUUID();
        organizacaoUuid = UUID.randomUUID();
        etapa = mock(EtapaCatequese.class);
        catequese = mock(Catequese.class);
        organizacao = mock(OrganizacaoEclesial.class);

        when(etapa.getUuid()).thenReturn(etapaUuid);
        when(etapa.getId()).thenReturn(30L);
        when(etapa.isAtivo()).thenReturn(true);
        when(etapa.getNome()).thenReturn("Primeira Etapa");
        when(etapa.getCatequese()).thenReturn(catequese);
        when(catequese.getOrganizacaoEclesial()).thenReturn(organizacao);
        when(organizacao.getId()).thenReturn(40L);
        when(organizacao.getUuid()).thenReturn(organizacaoUuid);
        when(organizacao.getNome()).thenReturn("Paróquia Teste");
        when(organizacao.isAtivo()).thenReturn(true);
        when(etapaRepository.findByUuid(etapaUuid)).thenReturn(Optional.of(etapa));
        when(organizacaoRepository.findByUuid(organizacaoUuid)).thenReturn(Optional.of(organizacao));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void deveCriarTurmaQuandoEtapaEOrganizacaoSaoCompativeis() {
        var response = service.criar(request(StatusCatequese.ATIVA, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 10, 31)));

        assertEquals("Turma A", response.nome());
        assertEquals(StatusCatequese.ATIVA, response.status());
        verify(repository).save(any());
    }

    @Test
    void deveRejeitarOrganizacaoInativa() {
        when(organizacao.isAtivo()).thenReturn(false);

        var ex = assertThrows(CatequeseBusinessException.class, () -> service.criar(request(null, null, null)));

        assertEquals("A organização eclesial está inativa.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarEtapaInativa() {
        when(etapa.isAtivo()).thenReturn(false);

        var ex = assertThrows(CatequeseBusinessException.class, () -> service.criar(request(null, null, null)));

        assertEquals("A etapa de catequese está inativa.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarTurmaEmOrganizacaoDiferenteDaCatequese() {
        var outraOrganizacao = mock(OrganizacaoEclesial.class);
        when(outraOrganizacao.getId()).thenReturn(999L);
        when(outraOrganizacao.getUuid()).thenReturn(organizacaoUuid);
        when(outraOrganizacao.isAtivo()).thenReturn(true);
        when(organizacaoRepository.findByUuid(organizacaoUuid)).thenReturn(Optional.of(outraOrganizacao));

        var ex = assertThrows(CatequeseBusinessException.class, () -> service.criar(request(null, null, null)));

        assertEquals("A turma deve pertencer à mesma organização da catequese.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarPeriodoInvertido() {
        var ex = assertThrows(CatequeseBusinessException.class,
                () -> service.criar(request(null, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 9, 1))));

        assertEquals("Data final não pode ser anterior à data inicial.", ex.getMessage());
        verifyNoInteractions(etapaRepository, organizacaoRepository, repository);
    }

    private TurmaCatequeseRequest request(StatusCatequese status, LocalDate inicio, LocalDate fim) {
        return new TurmaCatequeseRequest(etapaUuid, organizacaoUuid, "Turma A", 2026,
                TurnoCatequese.NOITE, inicio, fim, 20, "Sala 1", status, null);
    }
}
