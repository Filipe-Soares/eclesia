package br.com.admeclesial.application.relatorio;
import java.math.BigDecimal;
public record RelatorioProjetoResumo(long totalProjetos,BigDecimal recursosVinculados,BigDecimal recursosUtilizados,BigDecimal saldoDisponivel,boolean exibirFinanceiro) {}
