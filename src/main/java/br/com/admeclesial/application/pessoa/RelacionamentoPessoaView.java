package br.com.admeclesial.application.pessoa;

import br.com.admeclesial.domain.pessoa.TipoRelacionamentoPessoa;

import java.time.LocalDate;
import java.util.UUID;

public record RelacionamentoPessoaView(
        UUID relacionamentoUuid,
        UUID outraPessoaUuid,
        String outraPessoaNome,
        TipoRelacionamentoPessoa tipo,
        LocalDate dataInicio,
        LocalDate dataFim
) {
}
