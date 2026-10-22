package br.com.admeclesial.interfaces.web.organizacao;

import br.com.admeclesial.application.organizacao.*;
import br.com.admeclesial.domain.organizacao.TipoOrganizacaoEclesial;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/organizacoes-eclesiais")
public class OrganizacaoEclesialWebController {
    private final OrganizacaoEclesialApplicationService service;
    private final SegurancaOrganizacaoService seguranca;
    public OrganizacaoEclesialWebController(OrganizacaoEclesialApplicationService service, SegurancaOrganizacaoService seguranca) { this.service = service; this.seguranca = seguranca; }

    @GetMapping
    public String listar(Model model) { var permitidas = seguranca.organizacoesAutorizadas(); model.addAttribute("organizacoes", service.listar().stream().filter(o -> permitidas.contains(o.uuid())).toList()); return "organizacao/lista"; }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("request", new OrganizacaoRequest(null, null, null, null, null, null, null));
        model.addAttribute("tipos", TipoOrganizacaoEclesial.values());
        return "organizacao/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") OrganizacaoRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) { model.addAttribute("tipos", TipoOrganizacaoEclesial.values()); return "organizacao/form"; }
        var criada = service.criar(request);
        redirectAttributes.addFlashAttribute("successMessage", "Organização eclesial cadastrada com sucesso.");
        return "redirect:/organizacoes-eclesiais/" + criada.uuid();
    }

    @GetMapping("/{uuid}")
    public String detalhe(@PathVariable UUID uuid, Model model) { seguranca.validarAcesso(uuid); model.addAttribute("organizacao", service.buscar(uuid)); return "organizacao/detalhe"; }

    @GetMapping("/{uuid}/editar")
    public String editar(@PathVariable UUID uuid, Model model) {
        seguranca.validarAcesso(uuid);
        var org = service.buscar(uuid);
        var req = new OrganizacaoRequest(
                org.nome(),
                org.nomeReduzido(),
                org.tipo(),
                org.cnpj(),
                org.email(),
                org.telefone(),
                org.observacao()
        );
        model.addAttribute("organizacaoUuid", uuid);
        model.addAttribute("request", req);
        model.addAttribute("tipos", TipoOrganizacaoEclesial.values());
        return "organizacao/form";
    }

    @PostMapping("/{uuid}/editar")
    public String atualizar(@PathVariable UUID uuid, @Valid @ModelAttribute("request") OrganizacaoRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        seguranca.validarAcesso(uuid);
        if (bindingResult.hasErrors()) {
            model.addAttribute("organizacaoUuid", uuid);
            model.addAttribute("tipos", TipoOrganizacaoEclesial.values());
            return "organizacao/form";
        }
        try {
            service.atualizar(uuid, request);
            redirectAttributes.addFlashAttribute("successMessage", "Organização eclesial atualizada com sucesso.");
            return "redirect:/organizacoes-eclesiais/" + uuid;
        } catch (Exception ex) {
            model.addAttribute("organizacaoUuid", uuid);
            model.addAttribute("tipos", TipoOrganizacaoEclesial.values());
            model.addAttribute("errorMessage", ex.getMessage());
            return "organizacao/form";
        }
    }
}
