package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.ContaFinanceiraApplicationService;
import br.com.admeclesial.application.financeiro.TransferenciaFinanceiraApplicationService;
import br.com.admeclesial.application.financeiro.TransferenciaFinanceiraRequest;
import br.com.admeclesial.domain.financeiro.ContaFinanceira;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/financeiro/transferencias")
public class TransferenciaFinanceiraWebController {

    private final TransferenciaFinanceiraApplicationService service;
    private final ContaFinanceiraApplicationService contas;

    public TransferenciaFinanceiraWebController(TransferenciaFinanceiraApplicationService service,
                                                ContaFinanceiraApplicationService contas) {
        this.service = service;
        this.contas = contas;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("transferencias", service.listar());
        m.addAttribute("contasPorId", contas.listar().stream()
                .collect(Collectors.toMap(ContaFinanceira::getId, ContaFinanceira::getNome)));
        return "financeiro/transferencias/lista";
    }

    @GetMapping("/nova")
    public String nova(Model m) {
        if (!m.containsAttribute("request")) {
            m.addAttribute("request", new TransferenciaFinanceiraRequest(null, null, null, null, null));
        }
        preencher(m);
        return "financeiro/transferencias/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") TransferenciaFinanceiraRequest request,
                        BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            preencher(m);
            return "financeiro/transferencias/form";
        }
        try {
            service.criar(request);
            ra.addFlashAttribute("successMessage", "Transferência registrada com sucesso.");
            return "redirect:/financeiro/transferencias";
        } catch (RuntimeException ex) {
            m.addAttribute("businessError", ex.getMessage());
            preencher(m);
            return "financeiro/transferencias/form";
        }
    }

    private void preencher(Model m) {
        m.addAttribute("contas", contas.listar());
    }
}
