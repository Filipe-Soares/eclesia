package br.com.admeclesial.application.catequese;
import br.com.admeclesial.domain.catequese.StatusCatequese;
import jakarta.validation.constraints.*; import java.time.LocalDate; import java.util.UUID;
public record CatequeseRequest(@NotNull UUID organizacaoEclesialUuid,@NotBlank @Size(max=200) String nome,@Size(max=5000) String descricao,StatusCatequese status,LocalDate dataInicio,LocalDate dataFim,@Size(max=5000) String observacao) {}
