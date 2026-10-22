package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.ContaFinanceiraApplicationService;
import br.com.admeclesial.application.financeiro.ContaFinanceiraRequest;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.financeiro.TipoContaFinanceira;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/financeiro/contas")
public class ContaFinanceiraWebController {

    private final ContaFinanceiraApplicationService service;
    private final OrganizacaoEclesialRepository orgRepo;
    private final SegurancaOrganizacaoService seguranca;

    public ContaFinanceiraWebController(ContaFinanceiraApplicationService service,
                                        OrganizacaoEclesialRepository orgRepo,
                                        SegurancaOrganizacaoService seguranca) {
        this.service = service;
        this.orgRepo = orgRepo;
        this.seguranca = seguranca;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("contas", service.listar());
        return "financeiro/contas/lista";
    }

    @GetMapping("/nova")
    public String nova(Model m) {
        if (!m.containsAttribute("request")) {
            m.addAttribute("request", new ContaFinanceiraRequest(null, null, null, null, null, null, true));
        }
        preencher(m);
        return "financeiro/contas/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") ContaFinanceiraRequest request,
                        BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            preencher(m);
            return "financeiro/contas/form";
        }
        try {
            service.salvar(null, request.organizacaoEclesialUuid(), request.nome(), request.tipo(),
                    request.banco(), request.agencia(), request.numero(),
                    request.ativo() == null || request.ativo());
            ra.addFlashAttribute("successMessage", "Conta financeira cadastrada com sucesso.");
            return "redirect:/financeiro/contas";
        } catch (RuntimeException ex) {
            m.addAttribute("businessError", ex.getMessage());
            preencher(m);
            return "financeiro/contas/form";
        }
    }

    @GetMapping("/{uuid}/editar")
    public String editar(@PathVariable UUID uuid, Model m) {
        var conta = service.buscar(uuid);
        var orgUuid = orgRepo.findById(conta.getOrganizacaoEclesialId())
                .map(o -> o.getUuid()).orElse(null);
        m.addAttribute("request", new ContaFinanceiraRequest(orgUuid, conta.getNome(), conta.getTipo(),
                conta.getBanco(), conta.getAgencia(), conta.getNumero(), conta.getAtivo()));
        m.addAttribute("contaUuid", uuid);
        preencher(m);
        return "financeiro/contas/form";
    }

    @PostMapping("/{uuid}/editar")
    public String atualizar(@PathVariable UUID uuid,
                            @Valid @ModelAttribute("request") ContaFinanceiraRequest request,
                            BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            m.addAttribute("contaUuid", uuid);
            preencher(m);
            return "financeiro/contas/form";
        }
        try {
            service.salvar(uuid, request.organizacaoEclesialUuid(), request.nome(), request.tipo(),
                    request.banco(), request.agencia(), request.numero(),
                    request.ativo() == null || request.ativo());
            ra.addFlashAttribute("successMessage", "Conta financeira atualizada com sucesso.");
            return "redirect:/financeiro/contas";
        } catch (RuntimeException ex) {
            m.addAttribute("contaUuid", uuid);
            m.addAttribute("businessError", ex.getMessage());
            preencher(m);
            return "financeiro/contas/form";
        }
    }

    private void preencher(Model m) {
        m.addAttribute("tipos", TipoContaFinanceira.values());
        var autorizadas = seguranca.organizacoesAutorizadas();
        m.addAttribute("organizacoes", orgRepo.findAllByOrderByNomeAsc().stream()
                .filter(o -> autorizadas.contains(o.getUuid())).toList());
    }
}
