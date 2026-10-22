package br.com.admeclesial.interfaces.web.pessoa;

import br.com.admeclesial.application.pessoa.PessoaApplicationService;
import br.com.admeclesial.application.pessoa.PessoaRequest;
import br.com.admeclesial.application.pessoa.PessoaResponse;
import br.com.admeclesial.application.pessoa.MembroFamiliaApplicationService;
import br.com.admeclesial.application.pessoa.RelacionamentoPessoaApplicationService;
import br.com.admeclesial.domain.pessoa.TipoSexo;
import br.com.admeclesial.domain.pessoa.TipoRelacionamentoPessoa;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/pessoas")
public class PessoaWebController {
    private final PessoaApplicationService service;
    private final MembroFamiliaApplicationService membros;
    private final RelacionamentoPessoaApplicationService relacionamentos;
    public PessoaWebController(PessoaApplicationService service, MembroFamiliaApplicationService membros, RelacionamentoPessoaApplicationService relacionamentos) {
        this.service = service; this.membros = membros; this.relacionamentos = relacionamentos;
    }

    @GetMapping
    public String listar(Model model) { model.addAttribute("pessoas", service.listar()); return "pessoa/lista"; }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("pessoaRequest", new PessoaRequest(null, null, null, null, null, null, null, null));
        model.addAttribute("sexos", TipoSexo.values());
        return "pessoa/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("pessoaRequest") PessoaRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) { model.addAttribute("sexos", TipoSexo.values()); return "pessoa/form"; }
        PessoaResponse pessoa = service.criar(request);
        redirectAttributes.addFlashAttribute("successMessage", "Pessoa cadastrada com sucesso.");
        return "redirect:/pessoas/" + pessoa.uuid();
    }

    @GetMapping("/{uuid}")
    public String visualizar(@PathVariable UUID uuid, Model model) {
        model.addAttribute("pessoa", service.buscar(uuid));
        model.addAttribute("familiasDoMembro", membros.listarPorPessoaView(uuid));
        model.addAttribute("relacionamentos", relacionamentos.listarPorPessoaView(uuid));
        model.addAttribute("pessoas", service.listar());
        model.addAttribute("tiposRelacionamento", TipoRelacionamentoPessoa.values());
        return "pessoa/detalhe";
    }

    @GetMapping("/{uuid}/editar")
    public String editar(@PathVariable UUID uuid, Model model) {
        var pessoa = service.buscar(uuid);
        var request = new PessoaRequest(
                pessoa.nome(),
                pessoa.nomeSocial(),
                pessoa.dataNascimento(),
                pessoa.sexo(),
                pessoa.cpf(),
                pessoa.rg(),
                pessoa.email(),
                pessoa.observacao()
        );
        model.addAttribute("pessoaUuid", uuid);
        model.addAttribute("pessoaRequest", request);
        model.addAttribute("sexos", TipoSexo.values());
        return "pessoa/form";
    }

    @PostMapping("/{uuid}/editar")
    public String atualizar(@PathVariable UUID uuid, @Valid @ModelAttribute("pessoaRequest") PessoaRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pessoaUuid", uuid);
            model.addAttribute("sexos", TipoSexo.values());
            return "pessoa/form";
        }
        try {
            service.atualizar(uuid, request);
            redirectAttributes.addFlashAttribute("successMessage", "Pessoa atualizada com sucesso.");
            return "redirect:/pessoas/" + uuid;
        } catch (Exception ex) {
            model.addAttribute("pessoaUuid", uuid);
            model.addAttribute("sexos", TipoSexo.values());
            model.addAttribute("errorMessage", ex.getMessage());
            return "pessoa/form";
        }
    }
}
