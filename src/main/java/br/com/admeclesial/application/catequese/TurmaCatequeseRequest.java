package br.com.admeclesial.application.catequese;
import br.com.admeclesial.domain.catequese.*; import jakarta.validation.constraints.*; import java.time.LocalDate; import java.util.UUID;
public record TurmaCatequeseRequest(@NotNull UUID etapaCatequeseUuid,@NotNull UUID organizacaoEclesialUuid,@NotBlank @Size(max=150) String nome,@Min(2000) @Max(2200) Integer ano,TurnoCatequese turno,LocalDate dataInicio,LocalDate dataFim,@Positive Integer capacidade,@Size(max=255) String local,StatusCatequese status,@Size(max=5000) String observacao) {}
