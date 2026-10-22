package br.com.admeclesial.application.dashboard;

import java.time.LocalDate;

public record DashboardConsultaResponse(
        LocalDate inicio,
        LocalDate fim,
        long pessoasAtivas,
        long turmasAtivas,
        long inscritosAtivos,
        long sacramentosPeriodo,
        long projetosExecucao
) {}
