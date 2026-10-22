package br.com.admeclesial.application.pessoa;

import java.util.UUID;

public class PessoaNotFoundException extends RuntimeException {

    public PessoaNotFoundException(UUID uuid) {
        super("Pessoa não encontrada: " + uuid);
    }
}
