package br.com.admeclesial.infrastructure.persistence.catequese;

import br.com.admeclesial.domain.catequese.InscricaoCatequese;
import br.com.admeclesial.domain.catequese.StatusInscricaoCatequese;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface InscricaoCatequeseRepository extends JpaRepository<InscricaoCatequese, Long> {
    Optional<InscricaoCatequese> findByUuid(UUID uuid);
    List<InscricaoCatequese> findAllByTurmaCatequeseIdOrderByDataInscricaoAsc(Long turmaId);
    List<InscricaoCatequese> findAllByPessoaIdOrderByDataInscricaoDesc(Long pessoaId);

    @Query("""
        select count(i) > 0 from InscricaoCatequese i
        where i.pessoa.id = :pessoaId
          and i.status = :status
          and i.turmaCatequese.etapaCatequese.catequese.id = :catequeseId
          and i.turmaCatequese.etapaCatequese.id = :etapaId
    """)
    boolean existsInscricaoAtivaNaEtapa(Long pessoaId, Long catequeseId, Long etapaId, StatusInscricaoCatequese status);
}
