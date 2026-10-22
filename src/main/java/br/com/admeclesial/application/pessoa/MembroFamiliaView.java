package br.com.admeclesial.application.pessoa;

import br.com.admeclesial.domain.pessoa.TipoMembroFamilia;

import java.time.LocalDate;
import java.util.UUID;

public record MembroFamiliaView(
        UUID membroUuid,
        UUID familiaUuid,
        String familiaNome,
        UUID pessoaUuid,
        String pessoaNome,
        TipoMembroFamilia tipo,
        LocalDate dataInicio,
        LocalDate dataFim
) {
}
