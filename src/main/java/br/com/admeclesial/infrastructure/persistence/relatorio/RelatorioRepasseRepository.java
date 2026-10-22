package br.com.admeclesial.infrastructure.persistence.relatorio;
import br.com.admeclesial.domain.financeiro.ObrigacaoRepasse; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public interface RelatorioRepasseRepository extends JpaRepository<ObrigacaoRepasse,Long> {
 interface ItemProjection { UUID getUuid(); LocalDate getVencimento(); LocalDate getCompetencia(); String getDestino(); String getRegra(); BigDecimal getValorBase(); BigDecimal getValorExclusoes(); BigDecimal getValorDevido(); BigDecimal getValorPago(); BigDecimal getSaldo(); String getStatus(); Boolean getVencido(); }
 interface ResumoProjection { BigDecimal getTotalDevido(); BigDecimal getTotalPago(); BigDecimal getSaldoARepassar(); }
 String BASE="""
 with pago_conta as (
   select cp.obrigacao_repasse_id,coalesce(sum(a.valor),0) valor_pago
   from adm_eclesial.conta_a_pagar cp
   left join adm_eclesial.parcela_a_pagar pa on pa.conta_a_pagar_id=cp.id
   left join adm_eclesial.alocacao_pagamento a on a.parcela_a_pagar_id=pa.id
   where cp.obrigacao_repasse_id is not null and cp.status<>'CANCELADA'
   group by cp.obrigacao_repasse_id
 ), calc as (
   select ob.id,ob.uuid,ob.regra_repasse_id,ob.competencia,ob.data_vencimento vencimento,ob.valor_base,ob.valor_exclusoes,ob.valor_repasse valor_devido,ob.status,
     coalesce(pc.valor_pago,0) valor_pago,greatest(ob.valor_repasse-coalesce(pc.valor_pago,0),0) saldo
   from adm_eclesial.obrigacao_repasse ob left join pago_conta pc on pc.obrigacao_repasse_id=ob.id
 )
 """;
 @Query(value=BASE+"""
 select c.uuid,c.vencimento,c.competencia,d.nome destino,
   case when r.tipo_base='VALOR_FIXO' then 'Valor fixo' else concat('Percentual ',coalesce(r.percentual,0),'%') end regra,
   c.valor_base valorBase,c.valor_exclusoes valorExclusoes,c.valor_devido valorDevido,c.valor_pago valorPago,c.saldo,c.status,
   (c.vencimento<current_date and c.saldo>0 and c.status<>'CANCELADA') vencido
 from calc c join adm_eclesial.regra_repasse r on r.id=c.regra_repasse_id
 join adm_eclesial.organizacao_eclesial o on o.id=r.organizacao_origem_id join adm_eclesial.organizacao_eclesial d on d.id=r.organizacao_destino_id
 where o.uuid=:org and (c.vencimento>=coalesce(:inicio,c.vencimento)) and (c.vencimento<=coalesce(:fim,c.vencimento)) and (:status is null or c.status=:status)
   and (:somenteVencidos=false or (c.vencimento<current_date and c.saldo>0 and c.status<>'CANCELADA'))
 order by c.vencimento asc,c.id asc
 """,countQuery=BASE+"""
 select count(*) from calc c join adm_eclesial.regra_repasse r on r.id=c.regra_repasse_id join adm_eclesial.organizacao_eclesial o on o.id=r.organizacao_origem_id
 where o.uuid=:org and (c.vencimento>=coalesce(:inicio,c.vencimento)) and (c.vencimento<=coalesce(:fim,c.vencimento)) and (:status is null or c.status=:status)
   and (:somenteVencidos=false or (c.vencimento<current_date and c.saldo>0 and c.status<>'CANCELADA'))
 """,nativeQuery=true)
 Page<ItemProjection> pesquisar(@Param("org") UUID org,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim,@Param("status") String status,@Param("somenteVencidos") boolean somenteVencidos,Pageable pageable);
 @Query(value=BASE+"""
 select coalesce(sum(c.valor_devido),0) totalDevido,coalesce(sum(c.valor_pago),0) totalPago,coalesce(sum(c.saldo),0) saldoARepassar
 from calc c join adm_eclesial.regra_repasse r on r.id=c.regra_repasse_id join adm_eclesial.organizacao_eclesial o on o.id=r.organizacao_origem_id
 where o.uuid=:org and (c.vencimento>=coalesce(:inicio,c.vencimento)) and (c.vencimento<=coalesce(:fim,c.vencimento)) and (:status is null or c.status=:status)
   and (:somenteVencidos=false or (c.vencimento<current_date and c.saldo>0 and c.status<>'CANCELADA'))
 """,nativeQuery=true)
 ResumoProjection resumo(@Param("org") UUID org,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim,@Param("status") String status,@Param("somenteVencidos") boolean somenteVencidos);
}
