package br.com.admeclesial.interfaces.web.catequese;

import br.com.admeclesial.application.catequese.CatequeseApplicationService;
import br.com.admeclesial.application.catequese.CatequeseBusinessException;
import br.com.admeclesial.application.catequese.EtapaCatequeseApplicationService;
import br.com.admeclesial.application.catequese.EtapaCatequeseRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/catequeses/{catequeseUuid}/etapas")
public class EtapaCatequeseWebController {
    private final EtapaCatequeseApplicationService etapaService;
    private final CatequeseApplicationService catequeseService;

    public EtapaCatequeseWebController(EtapaCatequeseApplicationService etapaService,
                                       CatequeseApplicationService catequeseService) {
        this.etapaService = etapaService;
        this.catequeseService = catequeseService;
    }

    @GetMapping
    public String listar(@PathVariable UUID catequeseUuid, Model model) {
        model.addAttribute("catequese", catequeseService.buscar(catequeseUuid));
        model.addAttribute("etapas", etapaService.listar(catequeseUuid));
        return "etapa-catequese/lista";
    }

    @GetMapping("/nova")
    public String nova(@PathVariable UUID catequeseUuid, Model model) {
        catequeseService.buscar(catequeseUuid);
        prepararFormulario(model, catequeseUuid,
                new EtapaCatequeseRequest(catequeseUuid, null, null, null, true));
        return "etapa-catequese/form";
    }

    @PostMapping
    public String criar(@PathVariable UUID catequeseUuid,
                        @Valid @ModelAttribute("request") EtapaCatequeseRequest request,
                        BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, catequeseUuid, request);
            return "etapa-catequese/form";
        }
        try {
            etapaService.criar(request);
            return "redirect:/catequeses/" + catequeseUuid + "/etapas";
        } catch (CatequeseBusinessException ex) {
            bindingResult.reject("etapa.business", ex.getMessage());
            prepararFormulario(model, catequeseUuid, request);
            return "etapa-catequese/form";
        }
    }

    @GetMapping("/{uuid}/editar")
    public String editar(@PathVariable UUID catequeseUuid,
                         @PathVariable UUID uuid, Model model) {
        var etapa = etapaService.buscar(uuid);
        prepararFormulario(model, catequeseUuid,
                new EtapaCatequeseRequest(catequeseUuid, etapa.nome(), etapa.descricao(), etapa.ordem(), etapa.ativo()));
        model.addAttribute("uuid", uuid);
        model.addAttribute("edicao", true);
        return "etapa-catequese/form";
    }

    @PostMapping("/{uuid}")
    public String atualizar(@PathVariable UUID catequeseUuid,
                            @PathVariable UUID uuid,
                            @Valid @ModelAttribute("request") EtapaCatequeseRequest request,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, catequeseUuid, request);
            model.addAttribute("uuid", uuid);
            model.addAttribute("edicao", true);
            return "etapa-catequese/form";
        }
        try {
            etapaService.atualizar(uuid, request);
            return "redirect:/catequeses/" + catequeseUuid + "/etapas";
        } catch (CatequeseBusinessException ex) {
            bindingResult.reject("etapa.business", ex.getMessage());
            prepararFormulario(model, catequeseUuid, request);
            model.addAttribute("uuid", uuid);
            model.addAttribute("edicao", true);
            return "etapa-catequese/form";
        }
    }

    private void prepararFormulario(Model model, UUID catequeseUuid, EtapaCatequeseRequest request) {
        model.addAttribute("catequese", catequeseService.buscar(catequeseUuid));
        model.addAttribute("request", request);
        if (!model.containsAttribute("edicao")) {
            model.addAttribute("edicao", false);
        }
    }
}
