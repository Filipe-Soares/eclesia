package br.com.admeclesial.application.catequese;
import jakarta.validation.constraints.*; import java.time.LocalDate; import java.util.UUID;
public record CatequistaTurmaRequest(@NotNull UUID turmaCatequeseUuid,@NotNull UUID pessoaUuid,@NotNull LocalDate dataInicio,LocalDate dataFim,boolean principal,@Size(max=5000) String observacao) {}
