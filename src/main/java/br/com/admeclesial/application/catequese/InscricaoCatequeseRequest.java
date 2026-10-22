package br.com.admeclesial.application.catequese;
import br.com.admeclesial.domain.catequese.StatusInscricaoCatequese; import jakarta.validation.constraints.*; import java.time.LocalDate; import java.util.UUID;
public record InscricaoCatequeseRequest(@NotNull UUID turmaCatequeseUuid,@NotNull UUID pessoaUuid,LocalDate dataInscricao,StatusInscricaoCatequese status,@Size(max=5000) String observacao) {}
