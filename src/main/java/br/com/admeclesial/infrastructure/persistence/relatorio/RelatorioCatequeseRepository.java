package br.com.admeclesial.infrastructure.persistence.relatorio;
import br.com.admeclesial.domain.catequese.TurmaCatequese;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface RelatorioCatequeseRepository extends JpaRepository<TurmaCatequese,Long> {
 @Query(value="""
 select t.uuid as uuid,t.nome as turma,c.nome as catequese,e.nome as etapa,t.ano as ano,t.turno as turno,t.status as status,
        t.capacidade as capacidade,
        (select count(*) from adm_eclesial.inscricao_catequese i where i.turma_catequese_id=t.id and i.status='ATIVA') as inscritos,
        greatest(coalesce(t.capacidade,0)-(select count(*) from adm_eclesial.inscricao_catequese i where i.turma_catequese_id=t.id and i.status='ATIVA'),0) as vagas,
        coalesce((select string_agg(p.nome || case when ct.principal then ' (Principal)' else '' end, ', ' order by ct.principal desc,p.nome)
                  from adm_eclesial.catequista_turma ct join adm_eclesial.pessoa p on p.id=ct.pessoa_id
                  where ct.turma_catequese_id=t.id and (ct.data_fim is null or ct.data_fim>=current_date)),'') as catequistas
 from adm_eclesial.turma_catequese t
 join adm_eclesial.etapa_catequese e on e.id=t.etapa_catequese_id
 join adm_eclesial.catequese c on c.id=e.catequese_id
 join adm_eclesial.organizacao_eclesial o on o.id=t.organizacao_eclesial_id
 where o.uuid=:org and (:catequese is null or c.uuid=:catequese) and (:etapa is null or e.uuid=:etapa)
   and (:ano is null or t.ano=:ano) and (:status is null or t.status=:status) and (:turno is null or t.turno=:turno)
 order by t.ano desc nulls last,c.nome,e.ordem,t.nome
 """,countQuery="""
 select count(*) from adm_eclesial.turma_catequese t
 join adm_eclesial.etapa_catequese e on e.id=t.etapa_catequese_id
 join adm_eclesial.catequese c on c.id=e.catequese_id
 join adm_eclesial.organizacao_eclesial o on o.id=t.organizacao_eclesial_id
 where o.uuid=:org and (:catequese is null or c.uuid=:catequese) and (:etapa is null or e.uuid=:etapa)
   and (:ano is null or t.ano=:ano) and (:status is null or t.status=:status) and (:turno is null or t.turno=:turno)
 """,nativeQuery=true)
 Page<RelatorioTurmaProjection> consultar(@Param("org") UUID org,@Param("catequese") UUID catequese,@Param("etapa") UUID etapa,@Param("ano") Integer ano,@Param("status") String status,@Param("turno") String turno,Pageable pageable);

 @Query(value="""
 select count(*) as turmas,
        coalesce(sum((select count(*) from adm_eclesial.inscricao_catequese i where i.turma_catequese_id=t.id and i.status='ATIVA')),0) as inscritos,
        coalesce(sum(greatest(coalesce(t.capacidade,0)-(select count(*) from adm_eclesial.inscricao_catequese i where i.turma_catequese_id=t.id and i.status='ATIVA'),0)),0) as vagas
 from adm_eclesial.turma_catequese t
 join adm_eclesial.etapa_catequese e on e.id=t.etapa_catequese_id
 join adm_eclesial.catequese c on c.id=e.catequese_id
 join adm_eclesial.organizacao_eclesial o on o.id=t.organizacao_eclesial_id
 where o.uuid=:org and (:catequese is null or c.uuid=:catequese) and (:etapa is null or e.uuid=:etapa)
   and (:ano is null or t.ano=:ano) and (:status is null or t.status=:status) and (:turno is null or t.turno=:turno)
 """,nativeQuery=true)
 Object[] totais(@Param("org") UUID org,@Param("catequese") UUID catequese,@Param("etapa") UUID etapa,@Param("ano") Integer ano,@Param("status") String status,@Param("turno") String turno);

 @Query("select new br.com.admeclesial.application.relatorio.RelatorioOpcao(c.uuid,c.nome) from Catequese c where c.organizacaoEclesial.uuid=:org order by c.nome")
 List<br.com.admeclesial.application.relatorio.RelatorioOpcao> catequeses(@Param("org") UUID org);
 @Query("select new br.com.admeclesial.application.relatorio.RelatorioOpcao(e.uuid,e.nome) from EtapaCatequese e where e.catequese.uuid=:catequese order by e.ordem")
 List<br.com.admeclesial.application.relatorio.RelatorioOpcao> etapas(@Param("catequese") UUID catequese);
}
