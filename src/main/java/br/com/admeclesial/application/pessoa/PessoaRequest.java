package br.com.admeclesial.application.pessoa;

import br.com.admeclesial.domain.pessoa.TipoSexo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PessoaRequest(
        @NotBlank @Size(max = 200) String nome,
        @Size(max = 200) String nomeSocial,
        LocalDate dataNascimento,
        TipoSexo sexo,
        @Size(max = 11) String cpf,
        @Size(max = 30) String rg,
        @Email @Size(max = 200) String email,
        String observacao
) {
}
