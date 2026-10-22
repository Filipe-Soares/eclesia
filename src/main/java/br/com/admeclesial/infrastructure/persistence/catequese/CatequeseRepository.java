package br.com.admeclesial.infrastructure.persistence.catequese;

import br.com.admeclesial.domain.catequese.Catequese;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CatequeseRepository extends JpaRepository<Catequese, Long> {
    Optional<Catequese> findByUuid(UUID uuid);
    List<Catequese> findAllByOrderByNomeAsc();
}
