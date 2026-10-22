package br.com.admeclesial.application.sacramento; import br.com.admeclesial.domain.sacramento.*; import java.time.*; import java.util.UUID;
public record SacramentoResponse(UUID uuid,UUID organizacaoEclesialUuid,String organizacaoNome,TipoSacramento tipo,LocalDate dataCelebracao,String local,StatusSacramento status,String observacao,LocalDateTime dataRegistro) {}
