package br.com.admeclesial.application.projeto;
import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.util.UUID;
public record FonteRecursoProjetoRequest(@NotNull UUID recursoVinculadoUuid,@NotNull @DecimalMin("0.01") BigDecimal valorAlocado,String descricao) {}
