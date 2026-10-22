package br.com.admeclesial.interfaces.web.familia;

import br.com.admeclesial.application.pessoa.*;
import br.com.admeclesial.domain.pessoa.Familia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamiliaWebControllerTest {

    @Mock FamiliaApplicationService familias;
    @Mock MembroFamiliaApplicationService membros;
    @Mock PessoaApplicationService pessoas;

    private FamiliaWebController controller;

    @BeforeEach
    void setUp() {
        controller = new FamiliaWebController(familias, membros, pessoas);
    }

    @Test
    void deveRenderizarLista() {
        when(familias.listar()).thenReturn(List.of(new Familia()));
        var model = new ExtendedModelMap();
        assertEquals("familia/lista", controller.listar(model));
        assertTrue(model.containsAttribute("familias"));
    }

    @Test
    void deveRenderizarDetalheComMembros() {
        var uuid = UUID.randomUUID();
        when(familias.buscar(uuid)).thenReturn(new Familia());
        when(membros.listarPorFamiliaView(uuid)).thenReturn(List.of());
        when(pessoas.listar()).thenReturn(List.of());
        var model = new ExtendedModelMap();
        assertEquals("familia/detalhe", controller.detalhe(uuid, model));
        assertTrue(model.containsAttribute("familia"));
        assertTrue(model.containsAttribute("membros"));
        assertTrue(model.containsAttribute("tiposMembro"));
    }

    @Test
    void deveCriarERedirecionarParaDetalhe() {
        var familia = mock(Familia.class);
        when(familia.getUuid()).thenReturn(UUID.randomUUID());
        when(familias.criar(any())).thenReturn(familia);
        var binding = new BeanPropertyBindingResult(new FamiliaRequest("Família", true), "request");
        var ra = new RedirectAttributesModelMap();
        var view = controller.criar(new FamiliaRequest("Família", true), binding, new ExtendedModelMap(), ra);
        assertTrue(view.startsWith("redirect:/familias/"));
    }

    @Test
    void deveAdicionarMembro() {
        var uuid = UUID.randomUUID();
        var ra = new RedirectAttributesModelMap();
        var view = controller.adicionarMembro(uuid, UUID.randomUUID(),
                br.com.admeclesial.domain.pessoa.TipoMembroFamilia.RESPONSAVEL, java.time.LocalDate.now(), ra);
        assertEquals("redirect:/familias/" + uuid, view);
        verify(membros).criar(any());
    }
}
