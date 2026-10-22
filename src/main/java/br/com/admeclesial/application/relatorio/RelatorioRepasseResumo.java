package br.com.admeclesial.application.relatorio;
import java.math.BigDecimal;
public record RelatorioRepasseResumo(BigDecimal totalDevido, BigDecimal totalPago, BigDecimal saldoARepassar) {}
