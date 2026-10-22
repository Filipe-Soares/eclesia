package br.com.admeclesial.infrastructure.persistence.relatorio;
import br.com.admeclesial.domain.financeiro.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.math.BigDecimal; import java.time.LocalDate; import java.util.*;
public interface RelatorioContribuicaoRepository extends JpaRepository<Contribuicao,Long> {
 interface RegistroProjection { UUID getUuid(); LocalDate getData(); String getTipo(); String getCategoria(); String getContribuinte(); BigDecimal getValor(); String getMeioRecebimento(); String getDestinacao(); }
 interface DizimistaProjection { UUID getPessoaUuid(); String getPessoa(); Long getQuantidade(); BigDecimal getValorTotal(); LocalDate getUltimaContribuicao(); }
 interface ResumoProjection { Long getTotalContribuicoes(); BigDecimal getValorTotal(); }
 @Query(value="""
 select c.uuid as uuid,c.data_contribuicao as data,c.tipo as tipo,cf.nome as categoria,
        coalesce(p.nome,'Anônima / não identificada') as contribuinte,c.valor as valor,c.meio_recebimento as meioRecebimento,
        coalesce((select string_agg(coalesce(nullif(dc.descricao,''),dc.tipo), ', ' order by dc.id) from adm_eclesial.destinacao_contribuicao dc where dc.contribuicao_id=c.id),'—') as destinacao
 from adm_eclesial.contribuicao c join adm_eclesial.organizacao_eclesial o on o.id=c.organizacao_eclesial_id
 join adm_eclesial.categoria_financeira cf on cf.id=c.categoria_financeira_id left join adm_eclesial.pessoa p on p.id=c.pessoa_id
 where o.uuid=:org and (c.data_contribuicao>=coalesce(:inicio,c.data_contribuicao)) and (c.data_contribuicao<=coalesce(:fim,c.data_contribuicao))
 and (:tipo is null or c.tipo=:tipo) and (:categoria is null or cf.uuid=:categoria)
 and (:pessoa is null or lower(coalesce(p.nome,'')) like lower(concat('%',cast(:pessoa as text),'%')))
 order by c.data_contribuicao desc,c.id desc
 """, countQuery="""
 select count(*) from adm_eclesial.contribuicao c join adm_eclesial.organizacao_eclesial o on o.id=c.organizacao_eclesial_id
 join adm_eclesial.categoria_financeira cf on cf.id=c.categoria_financeira_id left join adm_eclesial.pessoa p on p.id=c.pessoa_id
 where o.uuid=:org and (c.data_contribuicao>=coalesce(:inicio,c.data_contribuicao)) and (c.data_contribuicao<=coalesce(:fim,c.data_contribuicao))
 and (:tipo is null or c.tipo=:tipo) and (:categoria is null or cf.uuid=:categoria)
 and (:pessoa is null or lower(coalesce(p.nome,'')) like lower(concat('%',cast(:pessoa as text),'%')))
 """, nativeQuery=true)
 Page<RegistroProjection> registros(@Param("org") UUID org,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim,@Param("tipo") String tipo,@Param("categoria") UUID categoria,@Param("pessoa") String pessoa,Pageable pageable);
 @Query(value="""
 select count(*) as totalContribuicoes,coalesce(sum(c.valor),0) as valorTotal from adm_eclesial.contribuicao c
 join adm_eclesial.organizacao_eclesial o on o.id=c.organizacao_eclesial_id join adm_eclesial.categoria_financeira cf on cf.id=c.categoria_financeira_id left join adm_eclesial.pessoa p on p.id=c.pessoa_id
 where o.uuid=:org and (c.data_contribuicao>=coalesce(:inicio,c.data_contribuicao)) and (c.data_contribuicao<=coalesce(:fim,c.data_contribuicao))
 and (:tipo is null or c.tipo=:tipo) and (:categoria is null or cf.uuid=:categoria)
 and (:pessoa is null or lower(coalesce(p.nome,'')) like lower(concat('%',cast(:pessoa as text),'%')))
 """,nativeQuery=true)
 ResumoProjection resumo(@Param("org") UUID org,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim,@Param("tipo") String tipo,@Param("categoria") UUID categoria,@Param("pessoa") String pessoa);
 @Query(value="""
 select p.uuid as pessoaUuid,p.nome as pessoa,count(*) as quantidade,coalesce(sum(c.valor),0) as valorTotal,max(c.data_contribuicao) as ultimaContribuicao
 from adm_eclesial.contribuicao c join adm_eclesial.organizacao_eclesial o on o.id=c.organizacao_eclesial_id join adm_eclesial.pessoa p on p.id=c.pessoa_id
 join adm_eclesial.categoria_financeira cf on cf.id=c.categoria_financeira_id
 where o.uuid=:org and c.dizimista_id is not null and (c.data_contribuicao>=coalesce(:inicio,c.data_contribuicao)) and (c.data_contribuicao<=coalesce(:fim,c.data_contribuicao))
 and (:categoria is null or cf.uuid=:categoria) and (:pessoa is null or lower(p.nome) like lower(concat('%',cast(:pessoa as text),'%')))
 group by p.uuid,p.nome order by p.nome
 """,countQuery="""
 select count(distinct p.id) from adm_eclesial.contribuicao c join adm_eclesial.organizacao_eclesial o on o.id=c.organizacao_eclesial_id join adm_eclesial.pessoa p on p.id=c.pessoa_id
 join adm_eclesial.categoria_financeira cf on cf.id=c.categoria_financeira_id
 where o.uuid=:org and c.dizimista_id is not null and (c.data_contribuicao>=coalesce(:inicio,c.data_contribuicao)) and (c.data_contribuicao<=coalesce(:fim,c.data_contribuicao))
 and (:categoria is null or cf.uuid=:categoria) and (:pessoa is null or lower(p.nome) like lower(concat('%',cast(:pessoa as text),'%')))
 """,nativeQuery=true)
 Page<DizimistaProjection> porDizimista(@Param("org") UUID org,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim,@Param("categoria") UUID categoria,@Param("pessoa") String pessoa,Pageable pageable);
}
