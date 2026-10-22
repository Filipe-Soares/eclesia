package br.com.admeclesial.application.relatorio;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record RelatorioContaPagarItem(UUID uuid, LocalDate vencimento, String fornecedorOrigem, String descricao, String status, BigDecimal valorTotal, BigDecimal valorPago, BigDecimal saldo, boolean vencida, boolean repasse) {}
