package br.com.admeclesial.interfaces.web.pessoa;

import br.com.admeclesial.application.pessoa.RelacionamentoPessoaApplicationService;
import br.com.admeclesial.application.pessoa.RelacionamentoPessoaRequest;
import br.com.admeclesial.domain.pessoa.TipoRelacionamentoPessoa;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Controller
public class RelacionamentoPessoaWebController {

    private final RelacionamentoPessoaApplicationService relacionamentos;

    public RelacionamentoPessoaWebController(RelacionamentoPessoaApplicationService relacionamentos) {
        this.relacionamentos = relacionamentos;
    }

    @GetMapping("/relacionamentos")
    public String listar(Model model) {
        model.addAttribute("relacionamentos", relacionamentos.listar());
        return "relacionamento/lista";
    }

    @PostMapping("/pessoas/{pessoaUuid}/relacionamentos")
    public String criar(@PathVariable UUID pessoaUuid,
                        @RequestParam UUID outraPessoaUuid,
                        @RequestParam TipoRelacionamentoPessoa tipo,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                        RedirectAttributes redirectAttributes) {
        try {
            relacionamentos.criar(new RelacionamentoPessoaRequest(pessoaUuid, outraPessoaUuid, tipo, dataInicio, null, null));
            redirectAttributes.addFlashAttribute("successMessage", "Relacionamento registrado.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/pessoas/" + pessoaUuid;
    }

    @PostMapping("/pessoas/{pessoaUuid}/relacionamentos/{relacionamentoUuid}/remover")
    public String remover(@PathVariable UUID pessoaUuid,
                          @PathVariable UUID relacionamentoUuid,
                          RedirectAttributes redirectAttributes) {
        try {
            relacionamentos.remover(relacionamentoUuid);
            redirectAttributes.addFlashAttribute("successMessage", "Relacionamento removido.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/pessoas/" + pessoaUuid;
    }
}
