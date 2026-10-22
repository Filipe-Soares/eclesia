package br.com.admeclesial.infrastructure.persistence.financeiro;
import br.com.admeclesial.domain.financeiro.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface OrcamentoRepository extends JpaRepository<Orcamento,Long>{ Optional<Orcamento> findByUuid(UUID uuid); Optional<Orcamento> findByOrganizacaoEclesialIdAndAno(Long organizacaoId,Integer ano); List<Orcamento> findAllByOrderByAnoDesc(); }
