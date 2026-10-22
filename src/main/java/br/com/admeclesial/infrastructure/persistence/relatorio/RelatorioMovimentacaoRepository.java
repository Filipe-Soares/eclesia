package br.com.admeclesial.infrastructure.persistence.relatorio;
import br.com.admeclesial.domain.financeiro.MovimentacaoFinanceira; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public interface RelatorioMovimentacaoRepository extends JpaRepository<MovimentacaoFinanceira,Long> {
 interface ItemProjection { UUID getUuid(); LocalDate getData(); String getTipo(); String getConta(); String getCategoria(); String getCentroCusto(); String getDescricao(); BigDecimal getValor(); String getStatus(); }
 interface ResumoProjection { BigDecimal getTotalEntradas(); BigDecimal getTotalSaidas(); }
 @Query(value="""
 select m.uuid,m.data_movimentacao as data,m.tipo,c.nome as conta,coalesce(cat.nome,'—') as categoria,coalesce(cc.nome,'—') as centroCusto,coalesce(m.descricao,'—') as descricao,m.valor,m.status
 from adm_eclesial.movimentacao_financeira m join adm_eclesial.conta_financeira c on c.id=m.conta_financeira_id join adm_eclesial.organizacao_eclesial o on o.id=c.organizacao_eclesial_id
 left join adm_eclesial.categoria_financeira cat on cat.id=m.categoria_financeira_id left join adm_eclesial.centro_custo cc on cc.id=m.centro_custo_id
 where o.uuid=:org and (m.data_movimentacao>=coalesce(:inicio,m.data_movimentacao)) and (m.data_movimentacao<=coalesce(:fim,m.data_movimentacao))
 and (:conta is null or c.uuid=:conta) and (:tipo is null or m.tipo=:tipo) and (:categoria is null or cat.uuid=:categoria) and (:centro is null or cc.uuid=:centro)
 order by m.data_movimentacao desc,m.id desc
 """,countQuery="""
 select count(*) from adm_eclesial.movimentacao_financeira m join adm_eclesial.conta_financeira c on c.id=m.conta_financeira_id join adm_eclesial.organizacao_eclesial o on o.id=c.organizacao_eclesial_id
 left join adm_eclesial.categoria_financeira cat on cat.id=m.categoria_financeira_id left join adm_eclesial.centro_custo cc on cc.id=m.centro_custo_id
 where o.uuid=:org and (m.data_movimentacao>=coalesce(:inicio,m.data_movimentacao)) and (m.data_movimentacao<=coalesce(:fim,m.data_movimentacao))
 and (:conta is null or c.uuid=:conta) and (:tipo is null or m.tipo=:tipo) and (:categoria is null or cat.uuid=:categoria) and (:centro is null or cc.uuid=:centro)
 """,nativeQuery=true)
 Page<ItemProjection> pesquisar(@Param("org") UUID org,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim,@Param("conta") UUID conta,@Param("tipo") String tipo,@Param("categoria") UUID categoria,@Param("centro") UUID centro,Pageable pageable);
 @Query(value="""
 select coalesce(sum(case when m.tipo='ENTRADA' and m.status='CONFIRMADA' then m.valor else 0 end),0) as totalEntradas,
 coalesce(sum(case when m.tipo='SAIDA' and m.status='CONFIRMADA' then m.valor else 0 end),0) as totalSaidas
 from adm_eclesial.movimentacao_financeira m join adm_eclesial.conta_financeira c on c.id=m.conta_financeira_id join adm_eclesial.organizacao_eclesial o on o.id=c.organizacao_eclesial_id
 left join adm_eclesial.categoria_financeira cat on cat.id=m.categoria_financeira_id left join adm_eclesial.centro_custo cc on cc.id=m.centro_custo_id
 where o.uuid=:org and (m.data_movimentacao>=coalesce(:inicio,m.data_movimentacao)) and (m.data_movimentacao<=coalesce(:fim,m.data_movimentacao))
 and (:conta is null or c.uuid=:conta) and (:tipo is null or m.tipo=:tipo) and (:categoria is null or cat.uuid=:categoria) and (:centro is null or cc.uuid=:centro)
 """,nativeQuery=true)
 ResumoProjection resumo(@Param("org") UUID org,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim,@Param("conta") UUID conta,@Param("tipo") String tipo,@Param("categoria") UUID categoria,@Param("centro") UUID centro);
}
