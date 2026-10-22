package br.com.admeclesial.interfaces.web.catequese;

import br.com.admeclesial.application.catequese.CatequistaTurmaApplicationService;
import br.com.admeclesial.application.catequese.CatequistaTurmaRequest;
import br.com.admeclesial.application.catequese.CatequeseBusinessException;
import br.com.admeclesial.application.catequese.TurmaCatequeseApplicationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/catequeses/turmas/{turmaUuid}/catequistas")
public class CatequistaTurmaWebController {

    private final CatequistaTurmaApplicationService catequistaService;
    private final TurmaCatequeseApplicationService turmaService;

    public CatequistaTurmaWebController(CatequistaTurmaApplicationService catequistaService,
                                        TurmaCatequeseApplicationService turmaService) {
        this.catequistaService = catequistaService;
        this.turmaService = turmaService;
    }

    @GetMapping
    public String listar(@PathVariable UUID turmaUuid, Model model) {
        var turma = turmaService.buscar(turmaUuid);
        model.addAttribute("turma", turma);
        model.addAttribute("catequistas", catequistaService.listarPorTurma(turmaUuid));
        return "catequista-turma/lista";
    }

    @GetMapping("/novo")
    public String novo(@PathVariable UUID turmaUuid, Model model) {
        var turma = turmaService.buscar(turmaUuid);
        var dataInicio = turma.dataInicio() != null ? turma.dataInicio() : LocalDate.now();
        prepararFormulario(model, turmaUuid,
                new CatequistaTurmaRequest(turmaUuid, null, dataInicio, turma.dataFim(), false, null));
        return "catequista-turma/form";
    }

    @PostMapping
    public String criar(@PathVariable UUID turmaUuid,
                        @Valid @ModelAttribute("request") CatequistaTurmaRequest request,
                        BindingResult bindingResult,
                        Model model) {
        var turma = turmaService.buscar(turmaUuid);
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, turmaUuid, request);
            return "catequista-turma/form";
        }
        try {
            catequistaService.criar(new CatequistaTurmaRequest(
                    turmaUuid,
                    request.pessoaUuid(),
                    request.dataInicio(),
                    request.dataFim(),
                    request.principal(),
                    request.observacao()));
            return "redirect:/catequeses/turmas/" + turmaUuid + "/catequistas";
        } catch (CatequeseBusinessException ex) {
            bindingResult.reject("catequista.business", ex.getMessage());
            prepararFormulario(model, turmaUuid, request);
            return "catequista-turma/form";
        }
    }

    private void prepararFormulario(Model model, UUID turmaUuid, CatequistaTurmaRequest request) {
        var turma = turmaService.buscar(turmaUuid);
        model.addAttribute("turma", turma);
        model.addAttribute("request", request);
        model.addAttribute("pessoas", catequistaService.listarPessoasElegiveis(
                turmaUuid, request.dataInicio(), request.dataFim()));
    }
}
