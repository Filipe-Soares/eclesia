package br.com.admeclesial.application.catequese;
import br.com.admeclesial.domain.catequese.StatusCatequese; import java.time.LocalDate; import java.util.UUID;
public record CatequeseResponse(UUID uuid, UUID organizacaoUuid, String organizacaoNome, String nome, String descricao, StatusCatequese status, LocalDate dataInicio, LocalDate dataFim, String observacao) {}
