package br.com.admeclesial.application.dashboard;

import java.time.LocalDate;

public record DashboardCatequeseResponse(
        LocalDate inicio,
        LocalDate fim,
        long turmasAtivas,
        long inscritosAtivos,
        long vagasDisponiveis,
        long turmasSemCatequistaPrincipal,
        long turmasSemVagas) {
}
