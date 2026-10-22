package br.com.admeclesial.interfaces.web.catequese;

import br.com.admeclesial.application.catequese.CatequeseBusinessException;
import br.com.admeclesial.application.catequese.CatequeseApplicationService;
import br.com.admeclesial.application.catequese.EtapaCatequeseApplicationService;
import br.com.admeclesial.application.catequese.TurmaCatequeseApplicationService;
import br.com.admeclesial.application.catequese.TurmaCatequeseRequest;
import br.com.admeclesial.domain.catequese.StatusCatequese;
import br.com.admeclesial.domain.catequese.TurnoCatequese;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/catequeses/etapas/{etapaUuid}/turmas")
public class TurmaCatequeseWebController {
    private final TurmaCatequeseApplicationService turmaService;
    private final EtapaCatequeseApplicationService etapaService;
    private final CatequeseApplicationService catequeseService;

    public TurmaCatequeseWebController(TurmaCatequeseApplicationService turmaService,
                                       EtapaCatequeseApplicationService etapaService,
                                       CatequeseApplicationService catequeseService) {
        this.turmaService = turmaService;
        this.etapaService = etapaService;
        this.catequeseService = catequeseService;
    }

    @GetMapping
    public String listar(@PathVariable UUID etapaUuid, Model model) {
        var etapa = etapaService.buscar(etapaUuid);
        model.addAttribute("etapa", etapa);
        model.addAttribute("turmas", turmaService.listarPorEtapa(etapaUuid));
        return "turma-catequese/lista";
    }

    @GetMapping("/nova")
    public String nova(@PathVariable UUID etapaUuid, Model model) {
        var etapa = etapaService.buscar(etapaUuid);
        var organizacaoUuid = catequeseService.buscar(etapa.catequeseUuid()).organizacaoUuid();
        prepararFormulario(model, etapa, null,
                new TurmaCatequeseRequest(
                        etapaUuid,
                        organizacaoUuid,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        StatusCatequese.PLANEJADA,
                        null));
        return "turma-catequese/form";
    }

    @PostMapping
    public String criar(@PathVariable UUID etapaUuid,
                        @Valid @ModelAttribute("request") TurmaCatequeseRequest request,
                        BindingResult bindingResult,
                        Model model) {
        var etapa = etapaService.buscar(etapaUuid);
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, etapa, null, request);
            return "turma-catequese/form";
        }
        try {
            turmaService.criar(normalizarRequest(request, etapa));
            return "redirect:/catequeses/etapas/" + etapaUuid + "/turmas";
        } catch (CatequeseBusinessException ex) {
            bindingResult.reject("turma.business", ex.getMessage());
            prepararFormulario(model, etapa, null, request);
            return "turma-catequese/form";
        }
    }

    @GetMapping("/{uuid}/editar")
    public String editar(@PathVariable UUID etapaUuid,
                         @PathVariable UUID uuid,
                         Model model) {
        var etapa = etapaService.buscar(etapaUuid);
        var turma = turmaService.buscar(uuid);
        var request = new TurmaCatequeseRequest(
                etapaUuid,
                turma.organizacaoUuid(),
                turma.nome(),
                turma.ano(),
                turma.turno(),
                turma.dataInicio(),
                turma.dataFim(),
                turma.capacidade(),
                turma.local(),
                turma.status(),
                turma.observacao());
        prepararFormulario(model, etapa, uuid, request);
        return "turma-catequese/form";
    }

    @PostMapping("/{uuid}")
    public String atualizar(@PathVariable UUID etapaUuid,
                            @PathVariable UUID uuid,
                            @Valid @ModelAttribute("request") TurmaCatequeseRequest request,
                            BindingResult bindingResult,
                            Model model) {
        var etapa = etapaService.buscar(etapaUuid);
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, etapa, uuid, request);
            return "turma-catequese/form";
        }
        try {
            turmaService.atualizar(uuid, normalizarRequest(request, etapa));
            return "redirect:/catequeses/etapas/" + etapaUuid + "/turmas";
        } catch (CatequeseBusinessException ex) {
            bindingResult.reject("turma.business", ex.getMessage());
            prepararFormulario(model, etapa, uuid, request);
            return "turma-catequese/form";
        }
    }

    private TurmaCatequeseRequest normalizarRequest(TurmaCatequeseRequest request,
                                                     br.com.admeclesial.application.catequese.EtapaCatequeseResponse etapa) {
        return new TurmaCatequeseRequest(
                etapa.uuid(),
                catequeseService.buscar(etapa.catequeseUuid()).organizacaoUuid(),
                request.nome(),
                request.ano(),
                request.turno(),
                request.dataInicio(),
                request.dataFim(),
                request.capacidade(),
                request.local(),
                request.status(),
                request.observacao());
    }

    private void prepararFormulario(Model model,
                                    br.com.admeclesial.application.catequese.EtapaCatequeseResponse etapa,
                                    UUID uuid,
                                    TurmaCatequeseRequest request) {
        model.addAttribute("etapa", etapa);
        model.addAttribute("request", request);
        model.addAttribute("uuid", uuid);
        model.addAttribute("edicao", uuid != null);
        model.addAttribute("turnos", TurnoCatequese.values());
        model.addAttribute("statusList", StatusCatequese.values());
    }
}
