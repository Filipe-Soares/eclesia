package br.com.admeclesial.infrastructure.persistence.projeto;
import br.com.admeclesial.domain.projeto.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.time.LocalDate; import java.util.*;
public interface ProjetoRepository extends JpaRepository<Projeto,Long>{
 Optional<Projeto> findByUuid(UUID uuid); List<Projeto> findAllByOrderByDataInicioDesc();
 @Query("""
 select p from Projeto p where p.organizacaoEclesialId=:orgId
 and (:tipo is null or p.tipo=:tipo) and (:status is null or p.status=:status)
 and (p.dataFim is null or p.dataFim>=coalesce(:inicio,p.dataFim))
 and (p.dataInicio is null or p.dataInicio<=coalesce(:fim,p.dataInicio))
 and (:somente=false or ((select coalesce(sum(f.valorAlocado),0) from FonteRecursoProjeto f where f.projetoId=p.id)>0
   and (select coalesce(sum(u.valor),0) from UtilizacaoRecurso u where u.fonteRecursoProjetoId in (select f2.id from FonteRecursoProjeto f2 where f2.projetoId=p.id))=0))
 order by case p.status when br.com.admeclesial.domain.projeto.StatusProjeto.EM_EXECUCAO then 1 when br.com.admeclesial.domain.projeto.StatusProjeto.PLANEJADO then 2 when br.com.admeclesial.domain.projeto.StatusProjeto.CONCLUIDO then 3 else 4 end, p.dataInicio desc nulls last, p.nome asc
 """) Page<Projeto> pesquisarRelatorio(@Param("orgId") Long orgId,@Param("tipo") TipoProjeto tipo,@Param("status") StatusProjeto status,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim,@Param("somente") boolean somente,Pageable pageable);
}
