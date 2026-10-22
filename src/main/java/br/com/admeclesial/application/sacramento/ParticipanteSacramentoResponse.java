package br.com.admeclesial.application.sacramento; import br.com.admeclesial.domain.sacramento.*; import java.util.UUID;
public record ParticipanteSacramentoResponse(UUID uuid,UUID sacramentoUuid,UUID pessoaUuid,String pessoaNome,TipoParticipacaoSacramento tipoParticipacao,String observacao) {}
