package br.com.admeclesial.interfaces.web.organizacao;

import br.com.admeclesial.application.organizacao.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/vinculos-funcoes-eclesiais")
public class VinculoFuncaoWebController {
    private final VinculoFuncaoApplicationService service;
    private final VinculoPessoaOrganizacaoApplicationService vinculoService;
    private final FuncaoEclesialApplicationService funcaoService;

    public VinculoFuncaoWebController(VinculoFuncaoApplicationService service,
                                      VinculoPessoaOrganizacaoApplicationService vinculoService,
                                      FuncaoEclesialApplicationService funcaoService) {
        this.service = service; this.vinculoService = vinculoService; this.funcaoService = funcaoService;
    }

    @GetMapping
    public String listar(Model model) { model.addAttribute("funcoesVinculadas", service.listar()); return "funcao-vinculo/lista"; }

    @GetMapping("/novo")
    public String novo(Model model) { carregar(model, new VinculoFuncaoForm()); return "funcao-vinculo/form"; }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") VinculoFuncaoForm form, BindingResult result, Model model) {
        if (result.hasErrors()) { carregar(model, form); return "funcao-vinculo/form"; }
        try {
            var response = service.criar(new VinculoFuncaoRequest(form.getVinculoUuid(), form.getFuncaoUuid(), form.getDataInicio(), form.getDataFim(), form.isPrincipal(), form.getObservacao()));
            return "redirect:/vinculos-funcoes-eclesiais/" + response.uuid();
        } catch (VinculoBusinessException | VinculoNotFoundException | FuncaoEclesialNotFoundException ex) {
            model.addAttribute("businessError", ex.getMessage()); carregar(model, form); return "funcao-vinculo/form";
        }
    }

    @GetMapping("/{uuid}")
    public String detalhe(@PathVariable UUID uuid, Model model) { model.addAttribute("vinculoFuncao", service.buscar(uuid)); return "funcao-vinculo/detalhe"; }

    private void carregar(Model model, VinculoFuncaoForm form) {
        model.addAttribute("form", form);
        model.addAttribute("vinculos", vinculoService.listar());
        model.addAttribute("funcoes", funcaoService.listar());
    }

    public static class VinculoFuncaoForm {
        private UUID vinculoUuid; private UUID funcaoUuid; private LocalDate dataInicio = LocalDate.now(); private LocalDate dataFim;
        private boolean principal; private String observacao;
        public UUID getVinculoUuid(){return vinculoUuid;} public void setVinculoUuid(UUID v){vinculoUuid=v;}
        public UUID getFuncaoUuid(){return funcaoUuid;} public void setFuncaoUuid(UUID v){funcaoUuid=v;}
        public LocalDate getDataInicio(){return dataInicio;} public void setDataInicio(LocalDate v){dataInicio=v;}
        public LocalDate getDataFim(){return dataFim;} public void setDataFim(LocalDate v){dataFim=v;}
        public boolean isPrincipal(){return principal;} public void setPrincipal(boolean v){principal=v;}
        public String getObservacao(){return observacao;} public void setObservacao(String v){observacao=v;}
    }
}
