package br.com.admeclesial.application.sacramento; import br.com.admeclesial.domain.sacramento.TipoParticipacaoSacramento; import jakarta.validation.constraints.*; import java.util.UUID;
public record ParticipanteSacramentoRequest(@NotNull UUID sacramentoUuid,@NotNull UUID pessoaUuid,@NotNull TipoParticipacaoSacramento tipoParticipacao,@Size(max=4000) String observacao) {}
