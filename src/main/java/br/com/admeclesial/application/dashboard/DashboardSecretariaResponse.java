package br.com.admeclesial.application.dashboard;

import java.time.LocalDate;

public record DashboardSecretariaResponse(
        LocalDate inicio,
        LocalDate fim,
        long pessoasAtivas,
        long novosCadastros,
        long sacramentosRegistrados,
        long matrimoniosRegistrados,
        long turmasAtivas,
        long turmasSemCatequistaPrincipal) {
}
