package br.com.admeclesial.application.financeiro;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
public record ItemOrcamentoRequest(@NotNull UUID categoriaFinanceiraUuid,UUID centroCustoUuid,@NotBlank String descricao,@NotNull @DecimalMin("0.01") BigDecimal valorPlanejado) {}
