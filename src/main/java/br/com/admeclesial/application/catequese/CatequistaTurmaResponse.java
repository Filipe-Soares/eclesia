package br.com.admeclesial.application.catequese;
import java.time.LocalDate; import java.util.UUID;
public record CatequistaTurmaResponse(UUID uuid,UUID turmaUuid,UUID pessoaUuid,String pessoaNome,LocalDate dataInicio,LocalDate dataFim,boolean principal,String observacao) {}
