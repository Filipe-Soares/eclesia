package br.com.admeclesial.application.financeiro;
import jakarta.validation.constraints.*;
import java.util.UUID;
public record OrcamentoRequest(@NotNull UUID organizacaoEclesialUuid,@NotNull @Min(2000) @Max(2200) Integer ano,@NotBlank String nome,String observacao) {}
