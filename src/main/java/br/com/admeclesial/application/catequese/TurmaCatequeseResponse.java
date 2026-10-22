package br.com.admeclesial.application.catequese;
import br.com.admeclesial.domain.catequese.*; import java.time.LocalDate; import java.util.UUID;
public record TurmaCatequeseResponse(UUID uuid,UUID etapaUuid,String etapaNome,UUID organizacaoUuid,String organizacaoNome,String nome,Integer ano,TurnoCatequese turno,LocalDate dataInicio,LocalDate dataFim,Integer capacidade,String local,StatusCatequese status,String observacao) {}
