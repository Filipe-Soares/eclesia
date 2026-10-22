package br.com.admeclesial.interfaces.web.familia;

import br.com.admeclesial.application.pessoa.FamiliaApplicationService;
import br.com.admeclesial.application.pessoa.FamiliaRequest;
import br.com.admeclesial.application.pessoa.MembroFamiliaApplicationService;
import br.com.admeclesial.application.pessoa.MembroFamiliaRequest;
import br.com.admeclesial.application.pessoa.PessoaApplicationService;
import br.com.admeclesial.domain.pessoa.TipoMembroFamilia;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/familias")
public class FamiliaWebController {

    private final FamiliaApplicationService familias;
    private final MembroFamiliaApplicationService membros;
    private final PessoaApplicationService pessoas;

    public FamiliaWebController(FamiliaApplicationService familias,
                                MembroFamiliaApplicationService membros,
                                PessoaApplicationService pessoas) {
        this.familias = familias;
        this.membros = membros;
        this.pessoas = pessoas;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("familias", familias.listar());
        return "familia/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("request", new FamiliaRequest(null, Boolean.TRUE));
        return "familia/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") FamiliaRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "familia/form";
        try {
            var familia = familias.criar(request);
            redirectAttributes.addFlashAttribute("successMessage", "Família cadastrada com sucesso.");
            return "redirect:/familias/" + familia.getUuid();
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "familia/form";
        }
    }

    @GetMapping("/{uuid}")
    public String detalhe(@PathVariable UUID uuid, Model model) {
        var familia = familias.buscar(uuid);
        model.addAttribute("familia", familia);
        model.addAttribute("membros", membros.listarPorFamiliaView(uuid));
        model.addAttribute("pessoas", pessoas.listar());
        model.addAttribute("tiposMembro", TipoMembroFamilia.values());
        return "familia/detalhe";
    }

    @GetMapping("/{uuid}/editar")
    public String editar(@PathVariable UUID uuid, Model model) {
        var familia = familias.buscar(uuid);
        model.addAttribute("familiaUuid", uuid);
        model.addAttribute("request", new FamiliaRequest(familia.getNome(), familia.isAtivo()));
        return "familia/form";
    }

    @PostMapping("/{uuid}/editar")
    public String atualizar(@PathVariable UUID uuid, @Valid @ModelAttribute("request") FamiliaRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("familiaUuid", uuid);
            return "familia/form";
        }
        try {
            familias.atualizar(uuid, request);
            redirectAttributes.addFlashAttribute("successMessage", "Família atualizada com sucesso.");
            return "redirect:/familias/" + uuid;
        } catch (Exception ex) {
            model.addAttribute("familiaUuid", uuid);
            model.addAttribute("errorMessage", ex.getMessage());
            return "familia/form";
        }
    }

    @PostMapping("/{uuid}/inativar")
    public String inativar(@PathVariable UUID uuid, RedirectAttributes redirectAttributes) {
        try {
            familias.inativar(uuid);
            redirectAttributes.addFlashAttribute("successMessage", "Família inativada.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/familias/" + uuid;
    }

    @PostMapping("/{uuid}/membros")
    public String adicionarMembro(@PathVariable UUID uuid,
                                  @RequestParam UUID pessoaUuid,
                                  @RequestParam TipoMembroFamilia tipo,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                  RedirectAttributes redirectAttributes) {
        try {
            membros.criar(new MembroFamiliaRequest(uuid, pessoaUuid, tipo, dataInicio, null));
            redirectAttributes.addFlashAttribute("successMessage", "Membro adicionado à família.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/familias/" + uuid;
    }

    @PostMapping("/{uuid}/membros/{membroUuid}/encerrar")
    public String encerrarMembro(@PathVariable UUID uuid,
                                 @PathVariable UUID membroUuid,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                                 RedirectAttributes redirectAttributes) {
        try {
            membros.encerrar(membroUuid, dataFim);
            redirectAttributes.addFlashAttribute("successMessage", "Associação encerrada.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/familias/" + uuid;
    }
}
