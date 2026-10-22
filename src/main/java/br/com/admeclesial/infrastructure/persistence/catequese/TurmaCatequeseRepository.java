package br.com.admeclesial.infrastructure.persistence.catequese;

import br.com.admeclesial.domain.catequese.TurmaCatequese;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TurmaCatequeseRepository extends JpaRepository<TurmaCatequese, Long> {
    Optional<TurmaCatequese> findByUuid(UUID uuid);
    List<TurmaCatequese> findAllByOrderByAnoDescNomeAsc();
    List<TurmaCatequese> findAllByEtapaCatequeseIdOrderByNomeAsc(Long etapaId);
}
