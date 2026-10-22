package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.RegraRepasseApplicationService;
import br.com.admeclesial.application.financeiro.RegraRepasseRequest;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.financeiro.PeriodicidadeRepasse;
import br.com.admeclesial.domain.financeiro.TipoBaseRepasse;
import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/financeiro/regras-repasse")
public class RegraRepasseWebController {

    private final RegraRepasseApplicationService service;
    private final OrganizacaoEclesialRepository orgRepo;
    private final SegurancaOrganizacaoService seguranca;

    public RegraRepasseWebController(RegraRepasseApplicationService service,
                                     OrganizacaoEclesialRepository orgRepo,
                                     SegurancaOrganizacaoService seguranca) {
        this.service = service;
        this.orgRepo = orgRepo;
        this.seguranca = seguranca;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("regras", service.listar());
        m.addAttribute("orgsPorId", orgRepo.findAllByOrderByNomeAsc().stream()
                .collect(Collectors.toMap(OrganizacaoEclesial::getId, OrganizacaoEclesial::getNome)));
        return "financeiro/regras-repasse/lista";
    }

    @GetMapping("/nova")
    public String nova(Model m) {
        if (!m.containsAttribute("request")) {
            m.addAttribute("request", new RegraRepasseRequest(null, null, null, null, null, null, null, null, true, null));
        }
        preencher(m);
        return "financeiro/regras-repasse/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") RegraRepasseRequest request,
                        BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            preencher(m);
            return "financeiro/regras-repasse/form";
        }
        try {
            service.salvar(request);
            ra.addFlashAttribute("successMessage", "Regra de repasse cadastrada com sucesso.");
            return "redirect:/financeiro/regras-repasse";
        } catch (RuntimeException ex) {
            m.addAttribute("businessError", ex.getMessage());
            preencher(m);
            return "financeiro/regras-repasse/form";
        }
    }

    private void preencher(Model m) {
        var autorizadas = seguranca.organizacoesAutorizadas();
        m.addAttribute("organizacoes", orgRepo.findAllByOrderByNomeAsc().stream()
                .filter(o -> autorizadas.contains(o.getUuid())).toList());
        m.addAttribute("tiposBase", TipoBaseRepasse.values());
        m.addAttribute("periodicidades", PeriodicidadeRepasse.values());
    }
}
