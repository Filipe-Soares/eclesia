package br.com.admeclesial.application.projeto;
import br.com.admeclesial.domain.financeiro.TipoDestinacaoContribuicao; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.util.UUID;
public record DestinacaoContribuicaoRequest(@NotNull UUID contribuicaoUuid, UUID centroCustoUuid, @NotNull TipoDestinacaoContribuicao tipo, @Size(max=500) String descricao, @NotNull @DecimalMin("0.01") BigDecimal valor, boolean usoExclusivo) {}
