package br.com.admeclesial.interfaces.web.catequese;

import br.com.admeclesial.application.catequese.CatequeseApplicationService;
import br.com.admeclesial.application.catequese.CatequeseResponse;
import br.com.admeclesial.application.catequese.EtapaCatequeseApplicationService;
import br.com.admeclesial.application.catequese.EtapaCatequeseResponse;
import br.com.admeclesial.application.catequese.TurmaCatequeseApplicationService;
import br.com.admeclesial.application.catequese.TurmaCatequeseRequest;
import br.com.admeclesial.domain.catequese.StatusCatequese;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurmaCatequeseWebControllerTest {

    @Mock TurmaCatequeseApplicationService turmaService;
    @Mock EtapaCatequeseApplicationService etapaService;
    @Mock CatequeseApplicationService catequeseService;

    private TurmaCatequeseWebController controller;

    private final UUID etapaUuid = UUID.randomUUID();
    private final UUID catequeseUuid = UUID.randomUUID();
    private final UUID organizacaoUuid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        controller = new TurmaCatequeseWebController(turmaService, etapaService, catequeseService);
        when(etapaService.buscar(etapaUuid))
                .thenReturn(new EtapaCatequeseResponse(etapaUuid, catequeseUuid, "Catequese", "Etapa", null, 1, true));
        when(catequeseService.buscar(catequeseUuid))
                .thenReturn(new CatequeseResponse(catequeseUuid, organizacaoUuid, "Organização", "Catequese", null, null, null, null, null));
    }

    @Test
    void novaDevePreencherOrganizacaoDaEtapaParaVincular() {
        var model = new ExtendedModelMap();
        assertEquals("turma-catequese/form", controller.nova(etapaUuid, model));

        var request = (TurmaCatequeseRequest) model.getAttribute("request");
        assertNotNull(request, "request no model");
        assertEquals(etapaUuid, request.etapaCatequeseUuid());
        assertEquals(organizacaoUuid, request.organizacaoEclesialUuid(), "organizacao oculta deve estar preenchida");
    }

    @Test
    void criarDeveSalvarERedirecionar() {
        var request = new TurmaCatequeseRequest(etapaUuid, organizacaoUuid, "Turma São Pedro", 2026, null,
                null, null, null, null, StatusCatequese.PLANEJADA, null);
        var binding = new BeanPropertyBindingResult(request, "request");

        var view = controller.criar(etapaUuid, request, binding, new ExtendedModelMap());

        assertEquals("redirect:/catequeses/etapas/" + etapaUuid + "/turmas", view);
        verify(turmaService).criar(any(TurmaCatequeseRequest.class));
    }
}
