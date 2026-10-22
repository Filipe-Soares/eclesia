package br.com.admeclesial.interfaces.web.catequese;

import br.com.admeclesial.application.catequese.CatequeseApplicationService;
import br.com.admeclesial.application.catequese.CatequeseBusinessException;
import br.com.admeclesial.application.catequese.CatequeseRequest;
import br.com.admeclesial.application.organizacao.OrganizacaoEclesialApplicationService;
import br.com.admeclesial.domain.catequese.StatusCatequese;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/catequeses")
public class CatequeseWebController {
    private final CatequeseApplicationService catequeseService;
    private final OrganizacaoEclesialApplicationService organizacaoService;
    private final SegurancaOrganizacaoService seguranca;

    public CatequeseWebController(CatequeseApplicationService catequeseService,
                                  OrganizacaoEclesialApplicationService organizacaoService, SegurancaOrganizacaoService seguranca) {
        this.catequeseService = catequeseService;
        this.organizacaoService = organizacaoService;
        this.seguranca = seguranca;
    }

    @GetMapping
    public String listar(Model model) {
        var permitidas = seguranca.organizacoesAutorizadas(); model.addAttribute("catequeses", catequeseService.listar().stream().filter(c -> permitidas.contains(c.organizacaoUuid())).toList());
        return "catequese/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        prepararFormulario(model, new CatequeseRequest(null, null, null, StatusCatequese.PLANEJADA,
                null, null, null));
        return "catequese/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") CatequeseRequest request,
                        BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, request);
            return "catequese/form";
        }
        try {
            seguranca.validarAcesso(request.organizacaoEclesialUuid());
            var criada = catequeseService.criar(request);
            redirectAttributes.addFlashAttribute("successMessage", "Catequese cadastrada com sucesso.");
            return "redirect:/catequeses/" + criada.uuid();
        } catch (CatequeseBusinessException ex) {
            bindingResult.reject("catequese.business", ex.getMessage());
            prepararFormulario(model, request);
            return "catequese/form";
        }
    }

    @GetMapping("/{uuid}/editar")
    public String editar(@PathVariable UUID uuid, Model model) {
        var catequese = catequeseService.buscar(uuid);
        seguranca.validarAcesso(catequese.organizacaoUuid());
        prepararFormulario(model, new CatequeseRequest(catequese.organizacaoUuid(), catequese.nome(),
                catequese.descricao(), catequese.status(), catequese.dataInicio(), catequese.dataFim(),
                catequese.observacao()));
        model.addAttribute("uuid", uuid);
        model.addAttribute("edicao", true);
        return "catequese/form";
    }

    @PostMapping("/{uuid}")
    public String atualizar(@PathVariable UUID uuid,
                            @Valid @ModelAttribute("request") CatequeseRequest request,
                            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, request);
            model.addAttribute("uuid", uuid);
            model.addAttribute("edicao", true);
            return "catequese/form";
        }
        try {
            var atual = catequeseService.buscar(uuid);
            seguranca.validarAcesso(atual.organizacaoUuid());
            seguranca.validarAcesso(request.organizacaoEclesialUuid());
            catequeseService.atualizar(uuid, request);
            redirectAttributes.addFlashAttribute("successMessage", "Catequese atualizada com sucesso.");
            return "redirect:/catequeses/" + uuid;
        } catch (CatequeseBusinessException ex) {
            bindingResult.reject("catequese.business", ex.getMessage());
            prepararFormulario(model, request);
            model.addAttribute("uuid", uuid);
            model.addAttribute("edicao", true);
            return "catequese/form";
        }
    }

    @GetMapping("/{uuid}")
    public String detalhe(@PathVariable UUID uuid, Model model) {
        var catequese = catequeseService.buscar(uuid); seguranca.validarAcesso(catequese.organizacaoUuid()); model.addAttribute("catequese", catequese);
        return "catequese/detalhe";
    }

    private void prepararFormulario(Model model, CatequeseRequest request) {
        model.addAttribute("request", request);
        var permitidas = seguranca.organizacoesAutorizadas(); model.addAttribute("organizacoes", organizacaoService.listar().stream().filter(o -> permitidas.contains(o.uuid())).toList());
        model.addAttribute("status", StatusCatequese.values());
        if (!model.containsAttribute("edicao")) {
            model.addAttribute("edicao", false);
        }
    }
}
