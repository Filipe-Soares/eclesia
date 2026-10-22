package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.CategoriaFinanceiraApplicationService;
import br.com.admeclesial.application.financeiro.CentroCustoApplicationService;
import br.com.admeclesial.application.financeiro.ContaFinanceiraApplicationService;
import br.com.admeclesial.application.financeiro.MovimentacaoFinanceiraApplicationService;
import br.com.admeclesial.application.financeiro.MovimentacaoFinanceiraRequest;
import br.com.admeclesial.domain.financeiro.CategoriaFinanceira;
import br.com.admeclesial.domain.financeiro.ContaFinanceira;
import br.com.admeclesial.domain.financeiro.StatusMovimentacaoFinanceira;
import br.com.admeclesial.domain.financeiro.TipoMovimentacaoFinanceira;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/financeiro/movimentacoes")
public class MovimentacaoFinanceiraWebController {

    private final MovimentacaoFinanceiraApplicationService service;
    private final ContaFinanceiraApplicationService contas;
    private final CategoriaFinanceiraApplicationService categorias;
    private final CentroCustoApplicationService centros;

    public MovimentacaoFinanceiraWebController(MovimentacaoFinanceiraApplicationService service,
                                               ContaFinanceiraApplicationService contas,
                                               CategoriaFinanceiraApplicationService categorias,
                                               CentroCustoApplicationService centros) {
        this.service = service;
        this.contas = contas;
        this.categorias = categorias;
        this.centros = centros;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("movimentacoes", service.listar());
        m.addAttribute("contasPorId", contas.listar().stream()
                .collect(Collectors.toMap(ContaFinanceira::getId, ContaFinanceira::getNome)));
        m.addAttribute("categoriasPorId", categorias.listar().stream()
                .collect(Collectors.toMap(CategoriaFinanceira::getId, CategoriaFinanceira::getNome)));
        return "financeiro/movimentacoes/lista";
    }

    @GetMapping("/nova")
    public String nova(Model m) {
        if (!m.containsAttribute("request")) {
            m.addAttribute("request", new MovimentacaoFinanceiraRequest(null, null, null, null, null, null, null, null, null));
        }
        preencher(m);
        return "financeiro/movimentacoes/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") MovimentacaoFinanceiraRequest request,
                        BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            preencher(m);
            return "financeiro/movimentacoes/form";
        }
        try {
            service.salvar(request);
            ra.addFlashAttribute("successMessage", "Movimentação financeira registrada com sucesso.");
            return "redirect:/financeiro/movimentacoes";
        } catch (RuntimeException ex) {
            m.addAttribute("businessError", ex.getMessage());
            preencher(m);
            return "financeiro/movimentacoes/form";
        }
    }

    private void preencher(Model m) {
        m.addAttribute("contas", contas.listar());
        m.addAttribute("categorias", categorias.listar());
        m.addAttribute("centros", centros.listar());
        m.addAttribute("tipos", List.of(TipoMovimentacaoFinanceira.ENTRADA, TipoMovimentacaoFinanceira.SAIDA));
        m.addAttribute("status", StatusMovimentacaoFinanceira.values());
    }
}
