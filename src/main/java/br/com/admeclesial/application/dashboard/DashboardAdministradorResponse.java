package br.com.admeclesial.application.dashboard;
import java.math.BigDecimal; import java.time.LocalDate;
public record DashboardAdministradorResponse(LocalDate inicio,LocalDate fim,long pessoasAtivas,long turmasAtivas,long inscritosAtivos,long projetosExecucao,long sacramentosPeriodo,long turmasSemCatequistaPrincipal,BigDecimal entradas,BigDecimal saidas,BigDecimal resultado,long contribuicoes,BigDecimal valorContribuicoes,long contasVencidas,BigDecimal saldoContasVencidas,long repassesVencidos,BigDecimal saldoRepassesVencidos,long projetosRecursoSemUso){}
