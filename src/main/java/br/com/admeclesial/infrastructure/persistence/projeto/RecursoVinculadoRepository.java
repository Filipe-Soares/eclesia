package br.com.admeclesial.infrastructure.persistence.projeto;
import br.com.admeclesial.domain.projeto.RecursoVinculado; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface RecursoVinculadoRepository extends JpaRepository<RecursoVinculado,Long>{ Optional<RecursoVinculado> findByUuid(UUID uuid); Optional<RecursoVinculado> findByDestinacaoContribuicaoId(Long destinacaoId); List<RecursoVinculado> findAllByOrderByDataCriacaoDesc(); }
