package br.com.admeclesial.application.relatorio;
public record RelatorioCatequeseResumo(long totalTurmas,long totalInscritos,long totalVagas,RelatorioPagina<RelatorioTurmaItem> turmas) {}
