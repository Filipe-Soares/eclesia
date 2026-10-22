package br.com.admeclesial.application.organizacao;

import br.com.admeclesial.domain.organizacao.TipoOrganizacaoEclesial;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrganizacaoResponse(
        UUID uuid,
        String nome,
        String nomeReduzido,
        TipoOrganizacaoEclesial tipo,
        String cnpj,
        String email,
        String telefone,
        boolean ativo,
        LocalDateTime dataCadastro,
        String observacao
) {}
