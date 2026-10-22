package br.com.admeclesial.infrastructure.persistence.financeiro;
import br.com.admeclesial.domain.financeiro.MovimentacaoFinanceira; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface MovimentacaoFinanceiraRepository extends JpaRepository<MovimentacaoFinanceira,Long>{ Optional<MovimentacaoFinanceira> findByUuid(UUID uuid); List<MovimentacaoFinanceira> findAllByOrderByDataMovimentacaoDesc(); }
