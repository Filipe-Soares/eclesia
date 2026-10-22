package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.CentroCustoApplicationService;
import br.com.admeclesial.application.financeiro.CentroCustoRequest;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/financeiro/centros-custo")
public class CentroCustoWebController {

    private final CentroCustoApplicationService service;
    private final OrganizacaoEclesialRepository orgRepo;
    private final SegurancaOrganizacaoService seguranca;

    public CentroCustoWebController(CentroCustoApplicationService service,
                                    OrganizacaoEclesialRepository orgRepo,
                                    SegurancaOrganizacaoService seguranca) {
        this.service = service;
        this.orgRepo = orgRepo;
        this.seguranca = seguranca;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("centros", service.listar());
        return "financeiro/centros-custo/lista";
    }

    @GetMapping("/novo")
    public String novo(Model m) {
        if (!m.containsAttribute("request")) {
            m.addAttribute("request", new CentroCustoRequest(null, null, null, true));
        }
        preencher(m);
        return "financeiro/centros-custo/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") CentroCustoRequest request,
                        BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            preencher(m);
            return "financeiro/centros-custo/form";
        }
        try {
            service.criar(request);
            ra.addFlashAttribute("successMessage", "Centro de custo cadastrado com sucesso.");
            return "redirect:/financeiro/centros-custo";
        } catch (RuntimeException ex) {
            m.addAttribute("businessError", ex.getMessage());
            preencher(m);
            return "financeiro/centros-custo/form";
        }
    }

    private void preencher(Model m) {
        var autorizadas = seguranca.organizacoesAutorizadas();
        m.addAttribute("organizacoes", orgRepo.findAllByOrderByNomeAsc().stream()
                .filter(o -> autorizadas.contains(o.getUuid())).toList());
    }
}
