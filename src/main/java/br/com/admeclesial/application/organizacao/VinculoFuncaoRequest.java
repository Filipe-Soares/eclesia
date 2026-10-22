package br.com.admeclesial.application.organizacao;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record VinculoFuncaoRequest(
        @NotNull UUID vinculoPessoaOrganizacaoUuid,
        @NotNull UUID funcaoEclesialUuid,
        @NotNull LocalDate dataInicio,
        LocalDate dataFim,
        boolean principal,
        String observacao
) {}
