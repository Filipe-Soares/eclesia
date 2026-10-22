package br.com.admeclesial.application.organizacao;

import br.com.admeclesial.domain.organizacao.TipoOrganizacaoEclesial;
import jakarta.validation.constraints.*;

public record OrganizacaoRequest(
        @NotBlank @Size(max = 200) String nome,
        @Size(max = 100) String nomeReduzido,
        @NotNull TipoOrganizacaoEclesial tipo,
        @Pattern(regexp = "^$|\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}$", message = "CNPJ deve conter 14 dígitos") String cnpj,
        @Email @Size(max = 200) String email,
        @Size(max = 30) String telefone,
        String observacao
) {}
