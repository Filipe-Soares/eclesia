package br.com.admeclesial.application.projeto;
import jakarta.validation.constraints.*; import java.util.UUID;
public record RecursoVinculadoRequest(@NotNull UUID destinacaoContribuicaoUuid,@NotBlank String finalidade,String observacao) {}
