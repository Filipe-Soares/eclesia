package br.com.admeclesial.interfaces.web.catequese;

import br.com.admeclesial.application.catequese.CatequeseBusinessException;
import br.com.admeclesial.application.catequese.InscricaoCatequeseApplicationService;
import br.com.admeclesial.application.catequese.InscricaoCatequeseRequest;
import br.com.admeclesial.application.catequese.TurmaCatequeseApplicationService;
import br.com.admeclesial.application.pessoa.PessoaApplicationService;
import br.com.admeclesial.domain.catequese.StatusInscricaoCatequese;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/catequeses/turmas/{turmaUuid}/inscricoes")
public class InscricaoCatequeseWebController {

    private final InscricaoCatequeseApplicationService inscricaoService;
    private final TurmaCatequeseApplicationService turmaService;
    private final PessoaApplicationService pessoaService;

    public InscricaoCatequeseWebController(InscricaoCatequeseApplicationService inscricaoService,
                                           TurmaCatequeseApplicationService turmaService,
                                           PessoaApplicationService pessoaService) {
        this.inscricaoService = inscricaoService;
        this.turmaService = turmaService;
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public String listar(@PathVariable UUID turmaUuid, Model model) {
        var turma = turmaService.buscar(turmaUuid);
        model.addAttribute("turma", turma);
        model.addAttribute("inscricoes", inscricaoService.listarPorTurma(turmaUuid));
        return "inscricao-catequese/lista";
    }

    @GetMapping("/nova")
    public String nova(@PathVariable UUID turmaUuid, Model model) {
        var turma = turmaService.buscar(turmaUuid);
        prepararFormulario(model, turmaUuid, new InscricaoCatequeseRequest(
                turmaUuid, null, LocalDate.now(), StatusInscricaoCatequese.ATIVA, null));
        return "inscricao-catequese/form";
    }

    @PostMapping
    public String criar(@PathVariable UUID turmaUuid,
                        @Valid @ModelAttribute("request") InscricaoCatequeseRequest request,
                        BindingResult bindingResult,
                        Model model) {
        var turma = turmaService.buscar(turmaUuid);
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, turmaUuid, request);
            return "inscricao-catequese/form";
        }
        try {
            inscricaoService.criar(new InscricaoCatequeseRequest(
                    turmaUuid,
                    request.pessoaUuid(),
                    request.dataInscricao(),
                    request.status(),
                    request.observacao()));
            return "redirect:/catequeses/turmas/" + turmaUuid + "/inscricoes";
        } catch (CatequeseBusinessException ex) {
            bindingResult.reject("inscricao.business", ex.getMessage());
            prepararFormulario(model, turmaUuid, request);
            return "inscricao-catequese/form";
        }
    }

    private void prepararFormulario(Model model, UUID turmaUuid, InscricaoCatequeseRequest request) {
        model.addAttribute("turma", turmaService.buscar(turmaUuid));
        model.addAttribute("request", request);
        model.addAttribute("pessoas", pessoaService.listar());
        model.addAttribute("statusList", StatusInscricaoCatequese.values());
    }
}
