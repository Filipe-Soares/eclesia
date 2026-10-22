package br.com.admeclesial.application.pessoa;

import br.com.admeclesial.domain.pessoa.TipoSexo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PessoaResponse(
        UUID uuid,
        String nome,
        String nomeSocial,
        LocalDate dataNascimento,
        TipoSexo sexo,
        String cpf,
        String rg,
        String email,
        String observacao,
        boolean ativo,
        LocalDateTime dataCadastro
) {
}
