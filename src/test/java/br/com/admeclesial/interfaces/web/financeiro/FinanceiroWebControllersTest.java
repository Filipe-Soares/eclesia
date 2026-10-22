package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.*;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.financeiro.*;
import br.com.admeclesial.infrastructure.persistence.financeiro.AlocacaoPagamentoRepository;
import br.com.admeclesial.infrastructure.persistence.financeiro.ParcelaAPagarRepository;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinanceiroWebControllersTest {

    @Mock ContaFinanceiraApplicationService contaService;
    @Mock CategoriaFinanceiraApplicationService categoriaService;
    @Mock CentroCustoApplicationService centroService;
    @Mock FornecedorApplicationService fornecedorService;
    @Mock DizimistaApplicationService dizimistaService;
    @Mock MovimentacaoFinanceiraApplicationService movimentacaoService;
    @Mock TransferenciaFinanceiraApplicationService transferenciaService;
    @Mock ContaAPagarApplicationService contaAPagarService;
    @Mock PagamentoApplicationService pagamentoService;
    @Mock RegraRepasseApplicationService regraService;
    @Mock ObrigacaoRepasseApplicationService obrigacaoService;
    @Mock OrcamentoApplicationService orcamentoService;
    @Mock OrganizacaoEclesialRepository orgRepo;
    @Mock PessoaRepository pessoaRepo;
    @Mock ParcelaAPagarRepository parcelaRepo;
    @Mock AlocacaoPagamentoRepository alocacaoRepo;
    @Mock SegurancaOrganizacaoService seguranca;

    ContaFinanceiraWebController contaController;
    CategoriaFinanceiraWebController categoriaController;
    CentroCustoWebController centroController;
    FornecedorWebController fornecedorController;
    DizimistaWebController dizimistaController;
    MovimentacaoFinanceiraWebController movimentacaoController;
    TransferenciaFinanceiraWebController transferenciaController;
    ContaAPagarWebController contaAPagarController;
    PagamentoWebController pagamentoController;
    RegraRepasseWebController regraController;
    ObrigacaoRepasseWebController obrigacaoController;
    OrcamentoWebController orcamentoController;

    @BeforeEach
    void setUp() {
        contaController = new ContaFinanceiraWebController(contaService, orgRepo, seguranca);
        categoriaController = new CategoriaFinanceiraWebController(categoriaService);
        centroController = new CentroCustoWebController(centroService, orgRepo, seguranca);
        fornecedorController = new FornecedorWebController(fornecedorService);
        dizimistaController = new DizimistaWebController(dizimistaService, orgRepo, pessoaRepo, seguranca);
        movimentacaoController = new MovimentacaoFinanceiraWebController(movimentacaoService, contaService, categoriaService, centroService);
        transferenciaController = new TransferenciaFinanceiraWebController(transferenciaService, contaService);
        contaAPagarController = new ContaAPagarWebController(contaAPagarService, fornecedorService, obrigacaoService, orgRepo, seguranca);
        pagamentoController = new PagamentoWebController(pagamentoService, contaService, contaAPagarService, parcelaRepo, alocacaoRepo);
        regraController = new RegraRepasseWebController(regraService, orgRepo, seguranca);
        obrigacaoController = new ObrigacaoRepasseWebController(obrigacaoService, regraService);
        orcamentoController = new OrcamentoWebController(orcamentoService, categoriaService, centroService, orgRepo, seguranca);
    }

    private BeanPropertyBindingResult binding(Object target) {
        return new BeanPropertyBindingResult(target, "request");
    }

    private void stubsOrganizacoesVazias() {
        when(seguranca.organizacoesAutorizadas()).thenReturn(Set.of());
        when(orgRepo.findAllByOrderByNomeAsc()).thenReturn(List.of());
    }

    private void stubsListasVazias() {
        when(contaService.listar()).thenReturn(List.of());
        when(categoriaService.listar()).thenReturn(List.of());
        when(centroService.listar()).thenReturn(List.of());
    }

    // ---- Contas financeiras ----

    @Test
    void contaFinanceira_criarSucesso_redireciona() {
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new ContaFinanceiraRequest(UUID.randomUUID(), "Caixa", TipoContaFinanceira.CAIXA, null, null, null, true);
        var view = contaController.criar(req, binding(req), m, ra);
        assertEquals("redirect:/financeiro/contas", view);
    }

    @Test
    void contaFinanceira_criarErroNegocio_exibeFormulario() {
        stubsOrganizacoesVazias();
        when(contaService.salvar(any(), any(), any(), any(), any(), any(), any(), anyBoolean()))
                .thenThrow(new IllegalArgumentException("Organização inativa."));
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new ContaFinanceiraRequest(UUID.randomUUID(), "Caixa", TipoContaFinanceira.CAIXA, null, null, null, true);
        var view = contaController.criar(req, binding(req), m, ra);
        assertEquals("financeiro/contas/form", view);
        assertEquals("Organização inativa.", m.get("businessError"));
    }

    // ---- Categorias financeiras ----

    @Test
    void categoriaFinanceira_criarSucesso_redireciona() {
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new CategoriaFinanceiraRequest("1", "Dízimo", "RECEITA", true);
        var view = categoriaController.criar(req, binding(req), m, ra);
        assertEquals("redirect:/financeiro/categorias", view);
    }

    @Test
    void categoriaFinanceira_criarErro_exibeFormulario() {
        when(categoriaService.criar(any())).thenThrow(new IllegalArgumentException("Código duplicado."));
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new CategoriaFinanceiraRequest("1", "Dízimo", "RECEITA", true);
        assertEquals("financeiro/categorias/form", categoriaController.criar(req, binding(req), m, ra));
        assertEquals("Código duplicado.", m.get("businessError"));
    }

    // ---- Centros de custo ----

    @Test
    void centroCusto_criarSucesso_redireciona() {
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new CentroCustoRequest(UUID.randomUUID(), "CC1", "Manutenção", true);
        assertEquals("redirect:/financeiro/centros-custo", centroController.criar(req, binding(req), m, ra));
    }

    @Test
    void centroCusto_criarErro_exibeFormulario() {
        stubsOrganizacoesVazias();
        when(centroService.criar(any())).thenThrow(new IllegalArgumentException("Centro inválido."));
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new CentroCustoRequest(UUID.randomUUID(), "CC1", "Manutenção", true);
        assertEquals("financeiro/centros-custo/form", centroController.criar(req, binding(req), m, ra));
        assertEquals("Centro inválido.", m.get("businessError"));
    }

    // ---- Fornecedores ----

    @Test
    void fornecedor_criarSucesso_redireciona() {
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new FornecedorRequest(TipoPessoaFornecedor.JURIDICA, "Papelaria", "123", null, null, true);
        assertEquals("redirect:/financeiro/fornecedores", fornecedorController.criar(req, binding(req), m, ra));
    }

    @Test
    void fornecedor_criarErro_exibeFormulario() {
        when(fornecedorService.criar(any())).thenThrow(new IllegalArgumentException("Documento duplicado."));
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new FornecedorRequest(TipoPessoaFornecedor.JURIDICA, "Papelaria", "123", null, null, true);
        assertEquals("financeiro/fornecedores/form", fornecedorController.criar(req, binding(req), m, ra));
        assertEquals("Documento duplicado.", m.get("businessError"));
    }

    // ---- Dizimistas ----

    @Test
    void dizimista_criarSucesso_redireciona() {
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new DizimistaRequest(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), null, true, null);
        assertEquals("redirect:/financeiro/dizimistas", dizimistaController.criar(req, binding(req), m, ra));
    }

    @Test
    void dizimista_criarErro_exibeFormulario() {
        stubsOrganizacoesVazias();
        when(pessoaRepo.findAllByOrderByNomeAsc()).thenReturn(List.of());
        when(dizimistaService.salvar(any(), any())).thenThrow(new IllegalArgumentException("Período inválido."));
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new DizimistaRequest(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), null, true, null);
        assertEquals("financeiro/dizimistas/form", dizimistaController.criar(req, binding(req), m, ra));
        assertEquals("Período inválido.", m.get("businessError"));
    }

    // ---- Movimentações ----

    @Test
    void movimentacao_criarSucesso_redireciona() {
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new MovimentacaoFinanceiraRequest(UUID.randomUUID(), null, null,
                TipoMovimentacaoFinanceira.ENTRADA, new BigDecimal("10.00"), LocalDate.now(), null, null, null);
        assertEquals("redirect:/financeiro/movimentacoes", movimentacaoController.criar(req, binding(req), m, ra));
    }

    @Test
    void movimentacao_transferenciaManual_rejeitada() {
        stubsListasVazias();
        when(movimentacaoService.salvar(any()))
                .thenThrow(new IllegalArgumentException("Transferência deve ser registrada pelo fluxo de transferência financeira."));
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new MovimentacaoFinanceiraRequest(UUID.randomUUID(), null, null,
                TipoMovimentacaoFinanceira.TRANSFERENCIA, new BigDecimal("10.00"), LocalDate.now(), null, null, null);
        assertEquals("financeiro/movimentacoes/form", movimentacaoController.criar(req, binding(req), m, ra));
        assertNotNull(m.get("businessError"));
    }

    // ---- Transferências ----

    @Test
    void transferencia_criarSucesso_redireciona() {
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new TransferenciaFinanceiraRequest(UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("50.00"), LocalDate.now(), null);
        assertEquals("redirect:/financeiro/transferencias", transferenciaController.criar(req, binding(req), m, ra));
    }

    @Test
    void transferencia_contasIguais_rejeitada() {
        when(contaService.listar()).thenReturn(List.of());
        when(transferenciaService.criar(any())).thenThrow(new IllegalArgumentException("Contas de origem e destino devem ser diferentes."));
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new TransferenciaFinanceiraRequest(UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("50.00"), LocalDate.now(), null);
        assertEquals("financeiro/transferencias/form", transferenciaController.criar(req, binding(req), m, ra));
        assertNotNull(m.get("businessError"));
    }

    // ---- Contas a pagar ----

    @Test
    void contaAPagar_criarSucesso_redireciona() {
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new ContaAPagarRequest(UUID.randomUUID(), UUID.randomUUID(), null, "Compra",
                LocalDate.now(), LocalDate.now().plusDays(5), new BigDecimal("100.00"));
        assertEquals("redirect:/financeiro/contas-a-pagar", contaAPagarController.criar(req, binding(req), m, ra));
    }

    @Test
    void contaAPagar_semFornecedorNemObrigacao_rejeitada() {
        stubsOrganizacoesVazias();
        when(fornecedorService.listar()).thenReturn(List.of());
        when(obrigacaoService.listar()).thenReturn(List.of());
        when(contaAPagarService.criar(any())).thenThrow(new IllegalArgumentException("Informe fornecedor ou obrigação de repasse."));
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new ContaAPagarRequest(UUID.randomUUID(), null, null, "Compra",
                LocalDate.now(), LocalDate.now().plusDays(5), new BigDecimal("100.00"));
        assertEquals("financeiro/contas-a-pagar/form", contaAPagarController.criar(req, binding(req), m, ra));
        assertNotNull(m.get("businessError"));
    }

    // ---- Pagamentos ----

    @Test
    void pagamento_criarSucesso_redireciona() {
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var form = new PagamentoWebController.PagamentoForm();
        form.setContaFinanceiraUuid(UUID.randomUUID());
        form.setValor(new BigDecimal("100.00"));
        form.setDataPagamento(LocalDate.now());
        form.setMeioPagamento(MeioPagamento.PIX);
        var linha = new PagamentoWebController.LinhaForm();
        linha.setParcelaAPagarUuid(UUID.randomUUID());
        linha.setValor(new BigDecimal("100.00"));
        form.setAlocacoes(List.of(linha));
        assertEquals("redirect:/financeiro/pagamentos", pagamentoController.criar(form, binding(form), m, ra));
    }

    @Test
    void pagamento_somaDivergente_rejeitada() {
        when(contaService.listar()).thenReturn(List.of());
        when(contaAPagarService.listar()).thenReturn(List.of());
        when(pagamentoService.criar(any())).thenThrow(new IllegalArgumentException("A soma das alocações deve ser igual ao valor do pagamento."));
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var form = new PagamentoWebController.PagamentoForm();
        form.setValor(new BigDecimal("100.00"));
        form.setDataPagamento(LocalDate.now());
        form.setMeioPagamento(MeioPagamento.PIX);
        var linha = new PagamentoWebController.LinhaForm();
        linha.setParcelaAPagarUuid(UUID.randomUUID());
        linha.setValor(new BigDecimal("50.00"));
        form.setAlocacoes(List.of(linha));
        assertEquals("financeiro/pagamentos/form", pagamentoController.criar(form, binding(form), m, ra));
        assertNotNull(m.get("businessError"));
    }

    // ---- Regras de repasse ----

    @Test
    void regraRepasse_criarSucesso_redireciona() {
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new RegraRepasseRequest(UUID.randomUUID(), UUID.randomUUID(), TipoBaseRepasse.DIZIMO,
                new BigDecimal("10"), null, PeriodicidadeRepasse.MENSAL, LocalDate.now(), null, true, null);
        assertEquals("redirect:/financeiro/regras-repasse", regraController.criar(req, binding(req), m, ra));
    }

    @Test
    void regraRepasse_configuracaoInconsistente_rejeitada() {
        stubsOrganizacoesVazias();
        when(regraService.salvar(any())).thenThrow(new IllegalArgumentException("Regra de valor fixo deve informar apenas valor fixo."));
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new RegraRepasseRequest(UUID.randomUUID(), UUID.randomUUID(), TipoBaseRepasse.VALOR_FIXO,
                new BigDecimal("10"), new BigDecimal("5"), PeriodicidadeRepasse.MENSAL, LocalDate.now(), null, true, null);
        assertEquals("financeiro/regras-repasse/form", regraController.criar(req, binding(req), m, ra));
        assertNotNull(m.get("businessError"));
    }

    // ---- Obrigações de repasse ----

    @Test
    void obrigacaoRepasse_gerarSucesso_redireciona() {
        when(regraService.listar()).thenReturn(List.of());
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new ObrigacaoRepasseRequest(UUID.randomUUID(), LocalDate.now(), new BigDecimal("1000"),
                new BigDecimal("100"), LocalDate.now().plusDays(3), null);
        assertEquals("redirect:/financeiro/obrigacoes-repasse", obrigacaoController.criar(req, binding(req), m, ra));
    }

    @Test
    void obrigacaoRepasse_exclusoesMaioresQueBase_rejeitada() {
        when(regraService.listar()).thenReturn(List.of());
        when(obrigacaoService.gerar(any())).thenThrow(new IllegalArgumentException("Exclusões não podem superar a base."));
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new ObrigacaoRepasseRequest(UUID.randomUUID(), LocalDate.now(), new BigDecimal("100"),
                new BigDecimal("200"), LocalDate.now().plusDays(3), null);
        assertEquals("financeiro/obrigacoes-repasse/form", obrigacaoController.criar(req, binding(req), m, ra));
        assertNotNull(m.get("businessError"));
    }

    // ---- Orçamento ----

    @Test
    void orcamento_criarSucesso_redirecionaParaDetalhe() {
        var orcamento = new Orcamento();
        when(orcamentoService.criar(any())).thenReturn(orcamento);
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new OrcamentoRequest(UUID.randomUUID(), 2026, "Anual", null);
        var view = orcamentoController.criar(req, binding(req), m, ra);
        assertTrue(view.startsWith("redirect:/financeiro/orcamentos/"));
    }

    @Test
    void orcamento_segundoMesmoAno_rejeitado() {
        stubsOrganizacoesVazias();
        when(orcamentoService.criar(any())).thenThrow(new IllegalArgumentException("Já existe orçamento para a organização no ano informado."));
        var m = new ExtendedModelMap();
        var ra = new RedirectAttributesModelMap();
        var req = new OrcamentoRequest(UUID.randomUUID(), 2026, "Anual", null);
        assertEquals("financeiro/orcamentos/form", orcamentoController.criar(req, binding(req), m, ra));
        assertNotNull(m.get("businessError"));
    }

    @Test
    void orcamento_aprovarSucesso_redirecionaComMensagem() {
        var ra = new RedirectAttributesModelMap();
        var uuid = UUID.randomUUID();
        assertEquals("redirect:/financeiro/orcamentos/" + uuid, orcamentoController.aprovar(uuid, ra));
    }

    @Test
    void orcamento_cancelarEncerrado_rejeitado() {
        when(orcamentoService.cancelar(any())).thenThrow(new IllegalArgumentException("Orçamento encerrado não pode ser cancelado."));
        var ra = new RedirectAttributesModelMap();
        var uuid = UUID.randomUUID();
        assertEquals("redirect:/financeiro/orcamentos/" + uuid, orcamentoController.cancelar(uuid, ra));
        assertNotNull(ra.getFlashAttributes().get("businessError"));
    }

    @Test
    void orcamento_adicionarItemErro_redirecionaComMensagem() {
        when(orcamentoService.adicionarItem(any(), any())).thenThrow(new IllegalArgumentException("Somente orçamento planejado pode receber itens."));
        var ra = new RedirectAttributesModelMap();
        var uuid = UUID.randomUUID();
        var item = new ItemOrcamentoRequest(UUID.randomUUID(), null, "Item", new BigDecimal("10.00"));
        assertEquals("redirect:/financeiro/orcamentos/" + uuid,
                orcamentoController.adicionarItem(uuid, item, binding(item), new ExtendedModelMap(), ra));
        assertNotNull(ra.getFlashAttributes().get("businessError"));
    }
}
