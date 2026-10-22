package br.com.admeclesial.application.relatorio;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record RelatorioRepasseItem(UUID uuid, LocalDate vencimento, LocalDate competencia, String destino, String regra, BigDecimal valorBase, BigDecimal valorExclusoes, BigDecimal valorDevido, BigDecimal valorPago, BigDecimal saldo, String status, boolean vencido) {}
