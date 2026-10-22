package br.com.admeclesial.application.relatorio;
import java.math.BigDecimal;
public record RelatorioMovimentacaoResumo(BigDecimal totalEntradas, BigDecimal totalSaidas, BigDecimal resultado) {}
