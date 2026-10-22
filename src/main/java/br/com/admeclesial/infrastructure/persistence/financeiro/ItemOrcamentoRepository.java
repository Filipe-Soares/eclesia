package br.com.admeclesial.infrastructure.persistence.financeiro;
import br.com.admeclesial.domain.financeiro.ItemOrcamento; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ItemOrcamentoRepository extends JpaRepository<ItemOrcamento,Long>{ Optional<ItemOrcamento> findByUuid(UUID uuid); List<ItemOrcamento> findAllByOrcamentoIdOrderByIdAsc(Long orcamentoId); }
