package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.CategoriaFinanceiraApplicationService;
import br.com.admeclesial.application.financeiro.CategoriaFinanceiraRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/financeiro/categorias")
public class CategoriaFinanceiraWebController {

    private final CategoriaFinanceiraApplicationService service;

    public CategoriaFinanceiraWebController(CategoriaFinanceiraApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("categorias", service.listar());
        return "financeiro/categorias/lista";
    }

    @GetMapping("/nova")
    public String nova(Model m) {
        if (!m.containsAttribute("request")) {
            m.addAttribute("request", new CategoriaFinanceiraRequest(null, null, null, true));
        }
        return "financeiro/categorias/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") CategoriaFinanceiraRequest request,
                        BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "financeiro/categorias/form";
        }
        try {
            service.criar(request);
            ra.addFlashAttribute("successMessage", "Categoria financeira cadastrada com sucesso.");
            return "redirect:/financeiro/categorias";
        } catch (RuntimeException ex) {
            m.addAttribute("businessError", ex.getMessage());
            return "financeiro/categorias/form";
        }
    }
}
