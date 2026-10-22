package br.com.admeclesial.application.sacramento; import jakarta.validation.constraints.*; import java.util.UUID;
public record RegistroSacramentalRequest(@NotNull UUID sacramentoUuid,@Size(max=50) String livro,@Size(max=50) String folha,@Size(max=50) String termo,@Size(max=50) String numeroRegistro,@Size(max=4000) String observacao) {}
