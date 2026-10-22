package br.com.admeclesial.application.catequese;
import jakarta.validation.constraints.*; import java.util.UUID;
public record EtapaCatequeseRequest(@NotNull UUID catequeseUuid,@NotBlank @Size(max=150) String nome,@Size(max=5000) String descricao,@NotNull @Positive Integer ordem,Boolean ativo) {}
