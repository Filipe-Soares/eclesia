package br.com.admeclesial.application.dashboard;
import java.math.BigDecimal; import java.time.LocalDate;
public record DashboardFinanceiroResponse(LocalDate inicio,LocalDate fim,BigDecimal entradas,BigDecimal saidas,BigDecimal resultado,long contribuicoes,BigDecimal valorContribuicoes,long contasVencidas,BigDecimal saldoContasVencidas,long repassesVencidos,BigDecimal saldoRepassesVencidos,long projetosExecucao,BigDecimal recursosVinculados,BigDecimal recursosUtilizados,BigDecimal saldoRecursos,long projetosRecursoSemUso){}
