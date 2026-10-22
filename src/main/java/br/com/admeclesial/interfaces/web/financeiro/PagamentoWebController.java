package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.AlocacaoPagamentoRequest;
import br.com.admeclesial.application.financeiro.ContaAPagarApplicationService;
import br.com.admeclesial.application.financeiro.ContaFinanceiraApplicationService;
import br.com.admeclesial.application.financeiro.PagamentoApplicationService;
import br.com.admeclesial.application.financeiro.PagamentoRequest;
import br.com.admeclesial.domain.financeiro.*;
import br.com.admeclesial.infrastructure.persistence.financeiro.AlocacaoPagamentoRepository;
import br.com.admeclesial.infrastructure.persistence.financeiro.ParcelaAPagarRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/financeiro/pagamentos")
public class PagamentoWebController {

    private final PagamentoApplicationService service;
    private final ContaFinanceiraApplicationService contas;
    private final ContaAPagarApplicationService contasPagar;
    private final ParcelaAPagarRepository parcelas;
    private final AlocacaoPagamentoRepository alocacoes;

    public PagamentoWebController(PagamentoApplicationService service,
                                  ContaFinanceiraApplicationService contas,
                                  ContaAPagarApplicationService contasPagar,
                                  ParcelaAPagarRepository parcelas,
                                  AlocacaoPagamentoRepository alocacoes) {
        this.service = service;
        this.contas = contas;
        this.contasPagar = contasPagar;
        this.parcelas = parcelas;
        this.alocacoes = alocacoes;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("pagamentos", service.listar());
        return "financeiro/pagamentos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model m) {
        var form = new PagamentoForm();
        form.setDataPagamento(LocalDate.now());
        form.setAlocacoes(montarLinhas());
        m.addAttribute("request", form);
        preencher(m);
        return "financeiro/pagamentos/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") PagamentoForm request,
                        BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            if (request.getAlocacoes() == null || request.getAlocacoes().isEmpty()) {
                request.setAlocacoes(montarLinhas());
            }
            preencher(m);
            return "financeiro/pagamentos/form";
        }
        try {
            var alocacoesValidas = new ArrayList<AlocacaoPagamentoRequest>();
            for (var linha : request.getAlocacoes()) {
                if (linha.getParcelaAPagarUuid() != null && linha.getValor() != null
                        && linha.getValor().signum() > 0) {
                    alocacoesValidas.add(new AlocacaoPagamentoRequest(linha.getParcelaAPagarUuid(), linha.getValor()));
                }
            }
            var req = new PagamentoRequest(request.getContaFinanceiraUuid(), request.getValor(),
                    request.getDataPagamento(), request.getMeioPagamento(), alocacoesValidas, request.getObservacao());
            service.criar(req);
            ra.addFlashAttribute("successMessage", "Pagamento registrado com sucesso.");
            return "redirect:/financeiro/pagamentos";
        } catch (RuntimeException ex) {
            m.addAttribute("businessError", ex.getMessage());
            request.setAlocacoes(montarLinhas());
            preencher(m);
            return "financeiro/pagamentos/form";
        }
    }

    private List<LinhaForm> montarLinhas() {
        var linhas = new ArrayList<LinhaForm>();
        for (var conta : contasPagar.listar()) {
            for (var parcela : parcelas.findAllByContaAPagarIdOrderByNumeroAsc(conta.getId())) {
                if (parcela.getStatus() != StatusParcelaAPagar.ABERTA
                        && parcela.getStatus() != StatusParcelaAPagar.VENCIDA) {
                    continue;
                }
                var pago = alocacoes.sumValorPorParcela(parcela.getId());
                var saldo = parcela.getValor().subtract(pago == null ? BigDecimal.ZERO : pago);
                if (saldo.signum() <= 0) {
                    continue;
                }
                var linha = new LinhaForm();
                linha.setParcelaAPagarUuid(parcela.getUuid());
                linha.setContaDescricao(conta.getDescricao());
                linha.setNumero(parcela.getNumero());
                linha.setVencimento(parcela.getVencimento());
                linha.setSaldo(saldo);
                linhas.add(linha);
            }
        }
        return linhas;
    }

    private void preencher(Model m) {
        m.addAttribute("contas", contas.listar());
        m.addAttribute("meios", MeioPagamento.values());
    }

    public static class PagamentoForm {
        private UUID contaFinanceiraUuid;
        private BigDecimal valor;
        private LocalDate dataPagamento;
        private MeioPagamento meioPagamento;
        private String observacao;
        private List<LinhaForm> alocacoes = new ArrayList<>();

        public UUID getContaFinanceiraUuid() { return contaFinanceiraUuid; }
        public void setContaFinanceiraUuid(UUID v) { contaFinanceiraUuid = v; }
        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal v) { valor = v; }
        public LocalDate getDataPagamento() { return dataPagamento; }
        public void setDataPagamento(LocalDate v) { dataPagamento = v; }
        public MeioPagamento getMeioPagamento() { return meioPagamento; }
        public void setMeioPagamento(MeioPagamento v) { meioPagamento = v; }
        public String getObservacao() { return observacao; }
        public void setObservacao(String v) { observacao = v; }
        public List<LinhaForm> getAlocacoes() { return alocacoes; }
        public void setAlocacoes(List<LinhaForm> v) { alocacoes = v; }
    }

    public static class LinhaForm {
        private UUID parcelaAPagarUuid;
        private BigDecimal valor;
        private String contaDescricao;
        private Integer numero;
        private LocalDate vencimento;
        private BigDecimal saldo;

        public UUID getParcelaAPagarUuid() { return parcelaAPagarUuid; }
        public void setParcelaAPagarUuid(UUID v) { parcelaAPagarUuid = v; }
        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal v) { valor = v; }
        public String getContaDescricao() { return contaDescricao; }
        public void setContaDescricao(String v) { contaDescricao = v; }
        public Integer getNumero() { return numero; }
        public void setNumero(Integer v) { numero = v; }
        public LocalDate getVencimento() { return vencimento; }
        public void setVencimento(LocalDate v) { vencimento = v; }
        public BigDecimal getSaldo() { return saldo; }
        public void setSaldo(BigDecimal v) { saldo = v; }
    }
}
