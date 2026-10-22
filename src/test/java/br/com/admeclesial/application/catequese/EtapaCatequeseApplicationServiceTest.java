package br.com.admeclesial.application.catequese;

import br.com.admeclesial.domain.catequese.Catequese;
import br.com.admeclesial.domain.catequese.EtapaCatequese;
import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.infrastructure.persistence.catequese.CatequeseRepository;
import br.com.admeclesial.infrastructure.persistence.catequese.EtapaCatequeseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EtapaCatequeseApplicationServiceTest {

    @Mock private EtapaCatequeseRepository repository;
    @Mock private CatequeseRepository catequeseRepository;
    @InjectMocks private EtapaCatequeseApplicationService service;

    private UUID catequeseUuid;
    private Catequese catequese;
    private OrganizacaoEclesial organizacao;

    @BeforeEach
    void setUp() {
        catequeseUuid = UUID.randomUUID();
        catequese = mock(Catequese.class);
        organizacao = mock(OrganizacaoEclesial.class);
        when(catequese.getUuid()).thenReturn(catequeseUuid);
        when(catequese.getOrganizacaoEclesial()).thenReturn(organizacao);
        when(organizacao.getUuid()).thenReturn(UUID.randomUUID());
        when(catequese.getId()).thenReturn(20L);
        when(catequese.getNome()).thenReturn("Catequese Eucarística");
        when(catequeseRepository.findByUuid(catequeseUuid)).thenReturn(Optional.of(catequese));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void deveCriarEtapaAtivaPorPadrao() {
        var response = service.criar(new EtapaCatequeseRequest(catequeseUuid, "  Primeira Etapa ", "", 1, null));

        assertEquals("Primeira Etapa", response.nome());
        assertTrue(response.ativo());
        verify(repository).save(any());
    }

    @Test
    void deveRejeitarNomeDuplicadoNaMesmaCatequese() {
        when(repository.existsByCatequeseIdAndNomeIgnoreCase(20L, "Primeira Etapa")).thenReturn(true);

        var ex = assertThrows(CatequeseBusinessException.class,
                () -> service.criar(new EtapaCatequeseRequest(catequeseUuid, "Primeira Etapa", null, 1, true)));

        assertEquals("Já existe uma etapa com esse nome nesta catequese.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarOrdemDuplicadaNaMesmaCatequese() {
        when(repository.existsByCatequeseIdAndOrdem(20L, 1)).thenReturn(true);

        var ex = assertThrows(CatequeseBusinessException.class,
                () -> service.criar(new EtapaCatequeseRequest(catequeseUuid, "Segunda Etapa", null, 1, true)));

        assertEquals("Já existe uma etapa com essa ordem nesta catequese.", ex.getMessage());
        verify(repository, never()).save(any());
    }
    @Test
    void deveAtualizarEtapaSemConsiderarElaMesmaComoDuplicada() {
        var etapaUuid = UUID.randomUUID();
        var etapa = mock(EtapaCatequese.class);
        when(etapa.getId()).thenReturn(30L);
        when(etapa.getUuid()).thenReturn(etapaUuid);
        when(etapa.getCatequese()).thenReturn(catequese);
        when(etapa.getNome()).thenReturn("Primeira Etapa");
        when(etapa.getDescricao()).thenReturn(null);
        when(etapa.getOrdem()).thenReturn(1);
        when(etapa.isAtivo()).thenReturn(true);
        when(repository.findByUuid(etapaUuid)).thenReturn(Optional.of(etapa));

        var response = service.atualizar(etapaUuid,
                new EtapaCatequeseRequest(catequeseUuid, "Primeira Etapa", "Descrição atualizada", 1, true));

        assertEquals(etapaUuid, response.uuid());
        verify(repository).existsByCatequeseIdAndNomeIgnoreCaseAndIdNot(20L, "Primeira Etapa", 30L);
        verify(repository).existsByCatequeseIdAndOrdemAndIdNot(20L, 1, 30L);
        verify(repository).save(etapa);
        verify(etapa).setDescricao("Descrição atualizada");
    }

    @Test
    void deveRejeitarAlteracaoParaNomeDuplicado() {
        var etapaUuid = UUID.randomUUID();
        var etapa = mock(EtapaCatequese.class);
        when(etapa.getId()).thenReturn(30L);
        when(etapa.getCatequese()).thenReturn(catequese);
        when(repository.findByUuid(etapaUuid)).thenReturn(Optional.of(etapa));
        when(repository.existsByCatequeseIdAndNomeIgnoreCaseAndIdNot(20L, "Segunda Etapa", 30L)).thenReturn(true);

        var ex = assertThrows(CatequeseBusinessException.class, () ->
                service.atualizar(etapaUuid,
                        new EtapaCatequeseRequest(catequeseUuid, "Segunda Etapa", null, 2, true)));

        assertEquals("Já existe uma etapa com esse nome nesta catequese.", ex.getMessage());
        verify(repository, never()).save(any());
    }

}
