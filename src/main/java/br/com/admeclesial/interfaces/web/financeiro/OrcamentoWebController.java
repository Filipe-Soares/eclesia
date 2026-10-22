package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.*;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.financeiro.CategoriaFinanceira;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/financeiro/orcamentos")
public class OrcamentoWebController {

    private final OrcamentoApplicationService service;
    private final CategoriaFinanceiraApplicationService categorias;
    private final CentroCustoApplicationService centros;
    private final OrganizacaoEclesialRepository orgRepo;
    private final SegurancaOrganizacaoService seguranca;

    public OrcamentoWebController(OrcamentoApplicationService service,
                                  CategoriaFinanceiraApplicationService categorias,
                                  CentroCustoApplicationService centros,
                                  OrganizacaoEclesialRepository orgRepo,
                                  SegurancaOrganizacaoService seguranca) {
        this.service = service;
        this.categorias = categorias;
        this.centros = centros;
        this.orgRepo = orgRepo;
        this.seguranca = seguranca;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("orcamentos", service.listar());
        return "financeiro/orcamentos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model m) {
        if (!m.containsAttribute("request")) {
            m.addAttribute("request", new OrcamentoRequest(null, null, null, null));
        }
        preencherOrganizacoes(m);
        return "financeiro/orcamentos/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") OrcamentoRequest request,
                        BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            preencherOrganizacoes(m);
            return "financeiro/orcamentos/form";
        }
        try {
            var criado = service.criar(request);
            ra.addFlashAttribute("successMessage", "Orçamento criado com sucesso.");
            return "redirect:/financeiro/orcamentos/" + criado.getUuid();
        } catch (RuntimeException ex) {
            m.addAttribute("businessError", ex.getMessage());
            preencherOrganizacoes(m);
            return "financeiro/orcamentos/form";
        }
    }

    @GetMapping("/{uuid}")
    public String detalhe(@PathVariable UUID uuid, Model m) {
        var orcamento = service.buscar(uuid);
        var itens = service.listarItens(uuid);
        m.addAttribute("orcamento", orcamento);
        m.addAttribute("itens", itens);
        m.addAttribute("categorias", categorias.listar());
        m.addAttribute("centros", centros.listar());
        m.addAttribute("categoriasPorId", categorias.listar().stream()
                .collect(Collectors.toMap(CategoriaFinanceira::getId, CategoriaFinanceira::getNome)));
        m.addAttribute("itemRequest", new ItemOrcamentoRequest(null, null, null, null));
        return "financeiro/orcamentos/detalhe";
    }

    @PostMapping("/{uuid}/itens")
    public String adicionarItem(@PathVariable UUID uuid,
                                @Valid @ModelAttribute("itemRequest") ItemOrcamentoRequest itemRequest,
                                BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("businessError", "Informe categoria, descrição e valor planejado do item.");
            return "redirect:/financeiro/orcamentos/" + uuid;
        }
        try {
            service.adicionarItem(uuid, itemRequest);
            ra.addFlashAttribute("successMessage", "Item adicionado ao orçamento.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("businessError", ex.getMessage());
        }
        return "redirect:/financeiro/orcamentos/" + uuid;
    }

    @PostMapping("/{uuid}/aprovar")
    public String aprovar(@PathVariable UUID uuid, RedirectAttributes ra) {
        return executarStatus(uuid, () -> service.aprovar(uuid), "Orçamento aprovado.", ra);
    }

    @PostMapping("/{uuid}/encerrar")
    public String encerrar(@PathVariable UUID uuid, RedirectAttributes ra) {
        return executarStatus(uuid, () -> service.encerrar(uuid), "Orçamento encerrado.", ra);
    }

    @PostMapping("/{uuid}/cancelar")
    public String cancelar(@PathVariable UUID uuid, RedirectAttributes ra) {
        return executarStatus(uuid, () -> service.cancelar(uuid), "Orçamento cancelado.", ra);
    }

    private String executarStatus(UUID uuid, Runnable acao, String sucesso, RedirectAttributes ra) {
        try {
            acao.run();
            ra.addFlashAttribute("successMessage", sucesso);
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("businessError", ex.getMessage());
        }
        return "redirect:/financeiro/orcamentos/" + uuid;
    }

    private void preencherOrganizacoes(Model m) {
        var autorizadas = seguranca.organizacoesAutorizadas();
        m.addAttribute("organizacoes", orgRepo.findAllByOrderByNomeAsc().stream()
                .filter(o -> autorizadas.contains(o.getUuid())).toList());
    }
}
