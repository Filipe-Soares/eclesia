package br.com.admeclesial.interfaces.web.pessoa;

import br.com.admeclesial.application.pessoa.RelacionamentoPessoaApplicationService;
import br.com.admeclesial.domain.pessoa.TipoRelacionamentoPessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelacionamentoPessoaWebControllerTest {

    @Mock RelacionamentoPessoaApplicationService relacionamentos;

    private RelacionamentoPessoaWebController controller;

    @BeforeEach
    void setUp() {
        controller = new RelacionamentoPessoaWebController(relacionamentos);
    }

    @Test
    void deveCriarRelacionamentoERedirecionar() {
        var pessoaUuid = UUID.randomUUID();
        var ra = new RedirectAttributesModelMap();
        var view = controller.criar(pessoaUuid, UUID.randomUUID(), TipoRelacionamentoPessoa.PAI, LocalDate.now(), ra);
        assertEquals("redirect:/pessoas/" + pessoaUuid, view);
        verify(relacionamentos).criar(any());
    }

    @Test
    void deveRemoverRelacionamento() {
        var pessoaUuid = UUID.randomUUID();
        var relUuid = UUID.randomUUID();
        var ra = new RedirectAttributesModelMap();
        var view = controller.remover(pessoaUuid, relUuid, ra);
        assertEquals("redirect:/pessoas/" + pessoaUuid, view);
        verify(relacionamentos).remover(relUuid);
    }

    @Test
    void deveRenderizarLista() {
        when(relacionamentos.listar()).thenReturn(List.of());
        var model = new ExtendedModelMap();
        assertEquals("relacionamento/lista", controller.listar(model));
        assertTrue(model.containsAttribute("relacionamentos"));
    }
}
