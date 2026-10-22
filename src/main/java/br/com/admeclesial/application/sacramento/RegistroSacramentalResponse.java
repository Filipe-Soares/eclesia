package br.com.admeclesial.application.sacramento; import java.util.UUID;
public record RegistroSacramentalResponse(UUID uuid,UUID sacramentoUuid,String livro,String folha,String termo,String numeroRegistro,String observacao) {}
