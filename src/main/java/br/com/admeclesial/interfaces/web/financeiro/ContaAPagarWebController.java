package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.ContaAPagarApplicationService;
import br.com.admeclesial.application.financeiro.ContaAPagarRequest;
import br.com.admeclesial.application.financeiro.FornecedorApplicationService;
import br.com.admeclesial.application.financeiro.ObrigacaoRepasseApplicationService;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.financeiro.Fornecedor;
import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/financeiro/contas-a-pagar")
public class ContaAPagarWebController {

    private final ContaAPagarApplicationService service;
    private final FornecedorApplicationService fornecedores;
    private final ObrigacaoRepasseApplicationService obrigacoes;
    private final OrganizacaoEclesialRepository orgRepo;
    private final SegurancaOrganizacaoService seguranca;

    public ContaAPagarWebController(ContaAPagarApplicationService service,
                                    FornecedorApplicationService fornecedores,
                                    ObrigacaoRepasseApplicationService obrigacoes,
                                    OrganizacaoEclesialRepository orgRepo,
                                    SegurancaOrganizacaoService seguranca) {
        this.service = service;
        this.fornecedores = fornecedores;
        this.obrigacoes = obrigacoes;
        this.orgRepo = orgRepo;
        this.seguranca = seguranca;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("contas", service.listar());
        m.addAttribute("orgsPorId", orgRepo.findAllByOrderByNomeAsc().stream()
                .collect(Collectors.toMap(OrganizacaoEclesial::getId, OrganizacaoEclesial::getNome)));
        m.addAttribute("fornecedoresPorId", fornecedores.listar().stream()
                .collect(Collectors.toMap(Fornecedor::getId, Fornecedor::getNome)));
        return "financeiro/contas-a-pagar/lista";
    }

    @GetMapping("/nova")
    public String nova(Model m) {
        if (!m.containsAttribute("request")) {
            m.addAttribute("request", new ContaAPagarRequest(null, null, null, null, null, null, null));
        }
        preencher(m);
        return "financeiro/contas-a-pagar/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") ContaAPagarRequest request,
                        BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            preencher(m);
            return "financeiro/contas-a-pagar/form";
        }
        try {
            service.criar(request);
            ra.addFlashAttribute("successMessage", "Conta a pagar cadastrada com sucesso. Primeira parcela gerada.");
            return "redirect:/financeiro/contas-a-pagar";
        } catch (RuntimeException ex) {
            m.addAttribute("businessError", ex.getMessage());
            preencher(m);
            return "financeiro/contas-a-pagar/form";
        }
    }

    private void preencher(Model m) {
        var autorizadas = seguranca.organizacoesAutorizadas();
        m.addAttribute("organizacoes", orgRepo.findAllByOrderByNomeAsc().stream()
                .filter(o -> autorizadas.contains(o.getUuid())).toList());
        m.addAttribute("fornecedores", fornecedores.listar());
        m.addAttribute("obrigacoes", obrigacoes.listar());
    }
}
