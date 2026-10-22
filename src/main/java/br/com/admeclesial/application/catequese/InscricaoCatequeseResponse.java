package br.com.admeclesial.application.catequese;
import br.com.admeclesial.domain.catequese.StatusInscricaoCatequese; import java.time.LocalDate; import java.util.UUID;
public record InscricaoCatequeseResponse(UUID uuid,UUID turmaUuid,UUID pessoaUuid,String pessoaNome,LocalDate dataInscricao,StatusInscricaoCatequese status,String observacao) {}
