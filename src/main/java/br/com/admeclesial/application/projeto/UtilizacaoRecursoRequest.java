package br.com.admeclesial.application.projeto;
import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.util.UUID;
public record UtilizacaoRecursoRequest(@NotNull UUID fonteRecursoProjetoUuid,@NotNull UUID movimentacaoFinanceiraUuid,@NotNull @DecimalMin("0.01") BigDecimal valor,String descricao) {}
