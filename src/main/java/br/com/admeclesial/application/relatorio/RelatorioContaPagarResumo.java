package br.com.admeclesial.application.relatorio;
import java.math.BigDecimal;
public record RelatorioContaPagarResumo(BigDecimal valorTotal, BigDecimal valorPago, BigDecimal saldoAPagar) {}
