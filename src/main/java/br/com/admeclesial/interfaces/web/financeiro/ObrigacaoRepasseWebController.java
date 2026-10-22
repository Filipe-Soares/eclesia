package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.ObrigacaoRepasseApplicationService;
import br.com.admeclesial.application.financeiro.ObrigacaoRepasseRequest;
import br.com.admeclesial.application.financeiro.RegraRepasseApplicationService;
import br.com.admeclesial.domain.financeiro.RegraRepasse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;

@Controller
@RequestMapping("/financeiro/obrigacoes-repasse")
public class ObrigacaoRepasseWebController {

    private final ObrigacaoRepasseApplicationService service;
    private final RegraRepasseApplicationService regras;

    public ObrigacaoRepasseWebController(ObrigacaoRepasseApplicationService service,
                                         RegraRepasseApplicationService regras) {
        this.service = service;
        this.regras = regras;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("obrigacoes", service.listar());
        var mapa = new HashMap<Long, String>();
        for (RegraRepasse r : regras.listar()) {
            mapa.put(r.getId(), r.getTipoBase() + " (" + r.getPeriodicidade() + ")");
        }
        m.addAttribute("regrasPorId", mapa);
        return "financeiro/obrigacoes-repasse/lista";
    }

    @GetMapping("/nova")
    public String nova(Model m) {
        if (!m.containsAttribute("request")) {
            m.addAttribute("request", new ObrigacaoRepasseRequest(null, null, null, null, null, null));
        }
        m.addAttribute("regras", regras.listar());
        return "financeiro/obrigacoes-repasse/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") ObrigacaoRepasseRequest request,
                        BindingResult bindingResult, Model m, RedirectAttributes ra) {
        m.addAttribute("regras", regras.listar());
        if (bindingResult.hasErrors()) {
            return "financeiro/obrigacoes-repasse/form";
        }
        try {
            service.gerar(request);
            ra.addFlashAttribute("successMessage", "Obrigação de repasse gerada com sucesso. Conta a pagar criada.");
            return "redirect:/financeiro/obrigacoes-repasse";
        } catch (RuntimeException ex) {
            m.addAttribute("businessError", ex.getMessage());
            return "financeiro/obrigacoes-repasse/form";
        }
    }
}
