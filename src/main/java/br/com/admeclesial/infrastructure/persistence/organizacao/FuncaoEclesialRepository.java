package br.com.admeclesial.infrastructure.persistence.organizacao;

import br.com.admeclesial.domain.organizacao.FuncaoEclesial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FuncaoEclesialRepository extends JpaRepository<FuncaoEclesial, Long> {
    Optional<FuncaoEclesial> findByUuid(UUID uuid);
    List<FuncaoEclesial> findAllByOrderByNomeAsc();
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndUuidNot(String codigo, UUID uuid);
}
