package br.com.admeclesial.application.projeto;
import br.com.admeclesial.domain.projeto.TipoProjeto; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record ProjetoRequest(@NotNull UUID organizacaoEclesialUuid, UUID itemOrcamentoUuid, @NotNull TipoProjeto tipo, @NotBlank String nome, String descricao, LocalDate dataInicio, LocalDate dataFim, @DecimalMin("0.00") BigDecimal valorEstimado) {}
