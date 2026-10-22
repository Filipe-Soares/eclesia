package br.com.admeclesial.interfaces.web.organizacao;

import br.com.admeclesial.application.organizacao.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/funcoes-eclesiais")
public class FuncaoEclesialWebController {
    private final FuncaoEclesialApplicationService service;
    public FuncaoEclesialWebController(FuncaoEclesialApplicationService service) { this.service = service; }

    @GetMapping
    public String listar(Model model) { model.addAttribute("funcoes", service.listar()); return "funcao/lista"; }

    @GetMapping("/nova")
    public String nova(Model model) { model.addAttribute("request", new FuncaoEclesialRequest(null, null, null, false)); return "funcao/form"; }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") FuncaoEclesialRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "funcao/form";
        try {
            return "redirect:/funcoes-eclesiais/" + service.criar(request).uuid();
        } catch (VinculoBusinessException ex) {
            bindingResult.reject("businessError", ex.getMessage());
            return "funcao/form";
        }
    }

    @GetMapping("/{uuid}/editar")
    public String editar(@PathVariable UUID uuid, Model model) {
        var funcao = service.buscar(uuid);
        model.addAttribute("funcaoUuid", uuid);
        model.addAttribute("request", new FuncaoEclesialRequest(funcao.codigo(), funcao.nome(), funcao.descricao(), funcao.exclusivoNaOrganizacao()));
        return "funcao/form";
    }

    @PostMapping("/{uuid}/editar")
    public String atualizar(@PathVariable UUID uuid, @Valid @ModelAttribute("request") FuncaoEclesialRequest request, BindingResult bindingResult, Model model) {
        model.addAttribute("funcaoUuid", uuid);
        if (bindingResult.hasErrors()) return "funcao/form";
        try {
            service.atualizar(uuid, request);
            return "redirect:/funcoes-eclesiais/" + uuid;
        } catch (VinculoBusinessException ex) {
            bindingResult.reject("businessError", ex.getMessage());
            return "funcao/form";
        }
    }

    @GetMapping("/{uuid}")
    public String detalhe(@PathVariable UUID uuid, Model model) { model.addAttribute("funcao", service.buscar(uuid)); return "funcao/detalhe"; }
}
