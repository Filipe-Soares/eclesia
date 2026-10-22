package br.com.admeclesial.application.organizacao;

import java.time.LocalDate;
import java.util.UUID;

public record VinculoFuncaoResponse(
        UUID uuid,
        UUID vinculoPessoaOrganizacaoUuid,
        UUID pessoaUuid,
        String pessoaNome,
        UUID organizacaoUuid,
        String organizacaoNome,
        UUID funcaoUuid,
        String funcaoCodigo,
        String funcaoNome,
        LocalDate dataInicio,
        LocalDate dataFim,
        boolean principal,
        String observacao
) {}
