package br.com.admeclesial.infrastructure.persistence.catequese;

import br.com.admeclesial.domain.catequese.EtapaCatequese;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface EtapaCatequeseRepository extends JpaRepository<EtapaCatequese, Long> {
    Optional<EtapaCatequese> findByUuid(UUID uuid);
    List<EtapaCatequese> findAllByCatequeseIdOrderByOrdemAsc(Long catequeseId);
    boolean existsByCatequeseIdAndNomeIgnoreCase(Long catequeseId, String nome);
    boolean existsByCatequeseIdAndOrdem(Long catequeseId, Integer ordem);
    boolean existsByCatequeseIdAndNomeIgnoreCaseAndIdNot(Long catequeseId, String nome, Long id);
    boolean existsByCatequeseIdAndOrdemAndIdNot(Long catequeseId, Integer ordem, Long id);
}
