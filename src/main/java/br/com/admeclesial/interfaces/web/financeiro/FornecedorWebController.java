package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.FornecedorApplicationService;
import br.com.admeclesial.application.financeiro.FornecedorRequest;
import br.com.admeclesial.domain.financeiro.TipoPessoaFornecedor;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/financeiro/fornecedores")
public class FornecedorWebController {

    private final FornecedorApplicationService service;

    public FornecedorWebController(FornecedorApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("fornecedores", service.listar());
        return "financeiro/fornecedores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model m) {
        if (!m.containsAttribute("request")) {
            m.addAttribute("request", new FornecedorRequest(null, null, null, null, null, true));
        }
        m.addAttribute("tiposPessoa", TipoPessoaFornecedor.values());
        return "financeiro/fornecedores/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") FornecedorRequest request,
                        BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            m.addAttribute("tiposPessoa", TipoPessoaFornecedor.values());
            return "financeiro/fornecedores/form";
        }
        try {
            service.criar(request);
            ra.addFlashAttribute("successMessage", "Fornecedor cadastrado com sucesso.");
            return "redirect:/financeiro/fornecedores";
        } catch (RuntimeException ex) {
            m.addAttribute("businessError", ex.getMessage());
            m.addAttribute("tiposPessoa", TipoPessoaFornecedor.values());
            return "financeiro/fornecedores/form";
        }
    }
}
