package br.com.admeclesial.application.relatorio;
import br.com.admeclesial.domain.catequese.StatusCatequese;
import br.com.admeclesial.domain.catequese.TurnoCatequese;
import java.util.UUID;
public record RelatorioCatequeseFiltro(UUID organizacaoUuid, UUID catequeseUuid, UUID etapaUuid, Integer ano, StatusCatequese status, TurnoCatequese turno, int pagina, int tamanho) {}
