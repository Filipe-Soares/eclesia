package br.com.admeclesial.application.organizacao;

import br.com.admeclesial.domain.organizacao.StatusVinculoPessoaOrganizacao;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record VinculoPessoaOrganizacaoRequest(
        @NotNull UUID pessoaUuid,
        @NotNull UUID organizacaoUuid,
        @NotNull LocalDate dataInicio,
        LocalDate dataFim,
        @NotNull StatusVinculoPessoaOrganizacao status,
        boolean principal,
        String observacao
) {}
