package br.com.admeclesial.application.organizacao;

import br.com.admeclesial.domain.organizacao.StatusVinculoPessoaOrganizacao;
import java.time.LocalDate;
import java.util.UUID;

public record VinculoPessoaOrganizacaoResponse(
        UUID uuid,
        UUID pessoaUuid,
        String pessoaNome,
        UUID organizacaoUuid,
        String organizacaoNome,
        LocalDate dataInicio,
        LocalDate dataFim,
        StatusVinculoPessoaOrganizacao status,
        boolean principal,
        String observacao
) {}
