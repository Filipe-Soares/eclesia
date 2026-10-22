package br.com.admeclesial.application.catequese;

import br.com.admeclesial.domain.catequese.StatusCatequese;
import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.infrastructure.persistence.catequese.CatequeseRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CatequeseApplicationServiceTest {

    @Mock private CatequeseRepository repository;
    @Mock private OrganizacaoEclesialRepository organizacaoRepository;
    @InjectMocks private CatequeseApplicationService service;

    private UUID organizacaoUuid;
    private OrganizacaoEclesial organizacao;

    @BeforeEach
    void setUp() {
        organizacaoUuid = UUID.randomUUID();
        organizacao = mock(OrganizacaoEclesial.class);
        when(organizacao.getUuid()).thenReturn(organizacaoUuid);
        when(organizacao.getId()).thenReturn(10L);
        when(organizacao.getNome()).thenReturn("Paróquia Teste");
        when(organizacao.isAtivo()).thenReturn(true);
        when(organizacaoRepository.findByUuid(organizacaoUuid)).thenReturn(Optional.of(organizacao));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void deveCriarCatequeseComStatusPlanejadaPorPadrao() {
        var response = service.criar(new CatequeseRequest(organizacaoUuid, "  Primeira Eucaristia  ", null,
                null, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 11, 30), ""));

        assertEquals("Primeira Eucaristia", response.nome());
        assertEquals(StatusCatequese.PLANEJADA, response.status());
        verify(repository).save(any());
    }

    @Test
    void deveRejeitarOrganizacaoInativa() {
        when(organizacao.isAtivo()).thenReturn(false);

        var ex = assertThrows(CatequeseBusinessException.class, () -> service.criar(request(null)));

        assertEquals("A organização eclesial está inativa.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarPeriodoFinalSemInicial() {
        var ex = assertThrows(CatequeseBusinessException.class, () -> service.criar(requestWithDates(null, LocalDate.of(2026, 12, 1))));

        assertEquals("Data inicial é obrigatória quando a data final for informada.", ex.getMessage());
        verifyNoInteractions(organizacaoRepository, repository);
    }

    @Test
    void deveRejeitarPeriodoInvertido() {
        var ex = assertThrows(CatequeseBusinessException.class, () -> service.criar(requestWithDates(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 1, 1))));

        assertEquals("Data final não pode ser anterior à data inicial.", ex.getMessage());
        verifyNoInteractions(organizacaoRepository, repository);
    }

    @Test
    void deveAtualizarCatequese() {
        var uuid = UUID.randomUUID();
        var existente = mock(br.com.admeclesial.domain.catequese.Catequese.class);
        when(existente.getUuid()).thenReturn(uuid);
        when(existente.getOrganizacaoEclesial()).thenReturn(organizacao);
        when(existente.getNome()).thenReturn("Catequese Atualizada");
        when(existente.getStatus()).thenReturn(StatusCatequese.ATIVA);
        when(existente.getDataInicio()).thenReturn(LocalDate.of(2026, 2, 1));
        when(existente.getDataFim()).thenReturn(LocalDate.of(2026, 11, 30));
        when(repository.findByUuid(uuid)).thenReturn(Optional.of(existente));

        var response = service.atualizar(uuid, new CatequeseRequest(organizacaoUuid, "Catequese Atualizada",
                "Descrição", StatusCatequese.ATIVA, LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 11, 30), "Observação"));

        assertEquals(uuid, response.uuid());
        assertEquals("Catequese Atualizada", response.nome());
        verify(existente).setNome("Catequese Atualizada");
        verify(existente).setStatus(StatusCatequese.ATIVA);
        verify(repository).save(existente);
    }

    @Test
    void deveRejeitarAtualizacaoDeCatequeseInexistente() {
        var uuid = UUID.randomUUID();
        when(repository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(CatequeseNotFoundException.class, () -> service.atualizar(uuid, request(null)));
        verify(repository, never()).save(any());
    }

    private CatequeseRequest request(String observacao) {
        return new CatequeseRequest(organizacaoUuid, "Catequese", null, StatusCatequese.ATIVA,
                LocalDate.of(2026, 1, 1), null, observacao);
    }

    private CatequeseRequest requestWithDates(LocalDate inicio, LocalDate fim) {
        return new CatequeseRequest(organizacaoUuid, "Catequese", null, null, inicio, fim, null);
    }
}
