package br.com.admeclesial.application.organizacao;

import java.util.UUID;

public class VinculoNotFoundException extends RuntimeException {
    public VinculoNotFoundException(UUID uuid) { super("Vínculo não encontrado: " + uuid); }
}
