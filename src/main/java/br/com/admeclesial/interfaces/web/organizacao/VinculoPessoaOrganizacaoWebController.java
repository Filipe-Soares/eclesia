package br.com.admeclesial.interfaces.web.organizacao;

import br.com.admeclesial.application.organizacao.*;
import br.com.admeclesial.application.pessoa.PessoaApplicationService;
import br.com.admeclesial.domain.organizacao.StatusVinculoPessoaOrganizacao;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/vinculos-pessoa-organizacao")
public class VinculoPessoaOrganizacaoWebController {
    private final VinculoPessoaOrganizacaoApplicationService service;
    private final PessoaApplicationService pessoaService;
    private final OrganizacaoEclesialApplicationService organizacaoService;
    private final SegurancaOrganizacaoService seguranca;

    public VinculoPessoaOrganizacaoWebController(VinculoPessoaOrganizacaoApplicationService service,
                                                 PessoaApplicationService pessoaService,
                                                 OrganizacaoEclesialApplicationService organizacaoService, SegurancaOrganizacaoService seguranca) {
        this.service = service; this.pessoaService = pessoaService; this.organizacaoService = organizacaoService; this.seguranca = seguranca;
    }

    @GetMapping
    public String listar(Model model) { var permitidas = seguranca.organizacoesAutorizadas(); model.addAttribute("vinculos", service.listar().stream().filter(v -> permitidas.contains(v.organizacaoUuid())).toList()); return "vinculo/lista"; }

    @GetMapping("/novo")
    public String novo(Model model) { carregarFormulario(model, new VinculoPessoaOrganizacaoForm()); return "vinculo/form"; }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") VinculoPessoaOrganizacaoForm form, BindingResult result, Model model) {
        if (result.hasErrors()) { carregarFormulario(model, form); return "vinculo/form"; }
        try {
            seguranca.validarAcesso(form.getOrganizacaoUuid());
            var response = service.criar(new VinculoPessoaOrganizacaoRequest(form.getPessoaUuid(), form.getOrganizacaoUuid(), form.getDataInicio(), form.getDataFim(), form.getStatus(), form.isPrincipal(), form.getObservacao()));
            return "redirect:/vinculos-pessoa-organizacao/" + response.uuid();
        } catch (VinculoBusinessException ex) {
            model.addAttribute("businessError", ex.getMessage()); carregarFormulario(model, form); return "vinculo/form";
        }
    }

    @GetMapping("/{uuid}/editar")
    public String editar(@PathVariable UUID uuid, Model model) {
        var vinculo = service.buscar(uuid);
        seguranca.validarAcesso(vinculo.organizacaoUuid());
        var form = new VinculoPessoaOrganizacaoForm();
        form.setPessoaUuid(vinculo.pessoaUuid());
        form.setOrganizacaoUuid(vinculo.organizacaoUuid());
        form.setDataInicio(vinculo.dataInicio());
        form.setDataFim(vinculo.dataFim());
        form.setStatus(vinculo.status());
        form.setPrincipal(vinculo.principal());
        form.setObservacao(vinculo.observacao());
        model.addAttribute("vinculoUuid", uuid);
        carregarFormulario(model, form);
        return "vinculo/form";
    }

    @PostMapping("/{uuid}/editar")
    public String atualizar(@PathVariable UUID uuid, @Valid @ModelAttribute("form") VinculoPessoaOrganizacaoForm form, BindingResult result, Model model) {
        model.addAttribute("vinculoUuid", uuid);
        if (result.hasErrors()) {
            carregarFormulario(model, form);
            return "vinculo/form";
        }
        try {
            seguranca.validarAcesso(form.getOrganizacaoUuid());
            service.atualizar(uuid, new VinculoPessoaOrganizacaoRequest(form.getPessoaUuid(), form.getOrganizacaoUuid(), form.getDataInicio(), form.getDataFim(), form.getStatus(), form.isPrincipal(), form.getObservacao()));
            return "redirect:/vinculos-pessoa-organizacao/" + uuid;
        } catch (VinculoBusinessException ex) {
            model.addAttribute("businessError", ex.getMessage());
            carregarFormulario(model, form);
            return "vinculo/form";
        }
    }

    @GetMapping("/{uuid}")
    public String detalhe(@PathVariable UUID uuid, Model model) { var vinculo = service.buscar(uuid); seguranca.validarAcesso(vinculo.organizacaoUuid()); model.addAttribute("vinculo", vinculo); return "vinculo/detalhe"; }

    private void carregarFormulario(Model model, VinculoPessoaOrganizacaoForm form) {
        model.addAttribute("form", form);
        model.addAttribute("pessoas", pessoaService.listar());
        var permitidas = seguranca.organizacoesAutorizadas(); model.addAttribute("organizacoes", organizacaoService.listar().stream().filter(o -> permitidas.contains(o.uuid())).toList());
        model.addAttribute("statusList", StatusVinculoPessoaOrganizacao.values());
    }

    public static class VinculoPessoaOrganizacaoForm {
        private UUID pessoaUuid; private UUID organizacaoUuid; private LocalDate dataInicio = LocalDate.now(); private LocalDate dataFim;
        private StatusVinculoPessoaOrganizacao status = StatusVinculoPessoaOrganizacao.ATIVO; private boolean principal; private String observacao;
        public UUID getPessoaUuid(){return pessoaUuid;} public void setPessoaUuid(UUID v){pessoaUuid=v;}
        public UUID getOrganizacaoUuid(){return organizacaoUuid;} public void setOrganizacaoUuid(UUID v){organizacaoUuid=v;}
        public LocalDate getDataInicio(){return dataInicio;} public void setDataInicio(LocalDate v){dataInicio=v;}
        public LocalDate getDataFim(){return dataFim;} public void setDataFim(LocalDate v){dataFim=v;}
        public StatusVinculoPessoaOrganizacao getStatus(){return status;} public void setStatus(StatusVinculoPessoaOrganizacao v){status=v;}
        public boolean isPrincipal(){return principal;} public void setPrincipal(boolean v){principal=v;}
        public String getObservacao(){return observacao;} public void setObservacao(String v){observacao=v;}
    }
}
