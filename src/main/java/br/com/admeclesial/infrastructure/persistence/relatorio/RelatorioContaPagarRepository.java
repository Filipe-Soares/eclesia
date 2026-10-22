package br.com.admeclesial.infrastructure.persistence.relatorio;
import br.com.admeclesial.domain.financeiro.ContaAPagar; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public interface RelatorioContaPagarRepository extends JpaRepository<ContaAPagar,Long> {
 interface ItemProjection { UUID getUuid(); LocalDate getVencimento(); String getFornecedorOrigem(); String getDescricao(); String getStatus(); BigDecimal getValorTotal(); BigDecimal getValorPago(); BigDecimal getSaldo(); Boolean getVencida(); Boolean getRepasse(); }
 interface ResumoProjection { BigDecimal getValorTotal(); BigDecimal getValorPago(); BigDecimal getSaldoAPagar(); }
 String BASE="""
 with pago_parcela as (
   select pa.id parcela_id, coalesce(sum(a.valor),0) pago from adm_eclesial.parcela_a_pagar pa left join adm_eclesial.alocacao_pagamento a on a.parcela_a_pagar_id=pa.id group by pa.id
 ), conta_calc as (
   select cp.id,cp.uuid,cp.organizacao_eclesial_id,cp.fornecedor_id,cp.obrigacao_repasse_id,cp.descricao,cp.status,cp.valor_total,
     coalesce(sum(pp.pago),0) valor_pago,
     greatest(cp.valor_total-coalesce(sum(pp.pago),0),0) saldo,
     coalesce(min(case when pa.status<>'CANCELADA' and pa.valor-coalesce(pp.pago,0)>0 then pa.vencimento end),cp.data_vencimento) vencimento,
     bool_or(pa.status<>'CANCELADA' and pa.vencimento<current_date and pa.valor-coalesce(pp.pago,0)>0) vencida
   from adm_eclesial.conta_a_pagar cp left join adm_eclesial.parcela_a_pagar pa on pa.conta_a_pagar_id=cp.id left join pago_parcela pp on pp.parcela_id=pa.id
   group by cp.id
 )
 """;
 @Query(value=BASE+"""
 select c.uuid,c.vencimento,case when c.fornecedor_id is not null then f.nome else 'Repasse para Cúria' end fornecedorOrigem,c.descricao,c.status,c.valor_total valorTotal,c.valor_pago valorPago,c.saldo,
   (coalesce(c.vencida,false) and c.status<>'CANCELADA' and c.saldo>0) vencida,(c.obrigacao_repasse_id is not null) repasse
 from conta_calc c join adm_eclesial.organizacao_eclesial o on o.id=c.organizacao_eclesial_id left join adm_eclesial.fornecedor f on f.id=c.fornecedor_id
 where o.uuid=:org and (c.vencimento>=coalesce(:inicio,c.vencimento)) and (c.vencimento<=coalesce(:fim,c.vencimento))
   and (:fornecedor is null or f.uuid=:fornecedor) and (:status is null or c.status=:status)
   and (:somenteVencidas=false or (coalesce(c.vencida,false) and c.status<>'CANCELADA' and c.saldo>0))
 order by c.vencimento asc,c.id asc
 """,countQuery=BASE+"""
 select count(*) from conta_calc c join adm_eclesial.organizacao_eclesial o on o.id=c.organizacao_eclesial_id left join adm_eclesial.fornecedor f on f.id=c.fornecedor_id
 where o.uuid=:org and (c.vencimento>=coalesce(:inicio,c.vencimento)) and (c.vencimento<=coalesce(:fim,c.vencimento))
   and (:fornecedor is null or f.uuid=:fornecedor) and (:status is null or c.status=:status)
   and (:somenteVencidas=false or (coalesce(c.vencida,false) and c.status<>'CANCELADA' and c.saldo>0))
 """,nativeQuery=true)
 Page<ItemProjection> pesquisar(@Param("org") UUID org,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim,@Param("fornecedor") UUID fornecedor,@Param("status") String status,@Param("somenteVencidas") boolean somenteVencidas,Pageable pageable);
 @Query(value=BASE+"""
 select coalesce(sum(c.valor_total),0) valorTotal,coalesce(sum(c.valor_pago),0) valorPago,coalesce(sum(c.saldo),0) saldoAPagar
 from conta_calc c join adm_eclesial.organizacao_eclesial o on o.id=c.organizacao_eclesial_id left join adm_eclesial.fornecedor f on f.id=c.fornecedor_id
 where o.uuid=:org and (c.vencimento>=coalesce(:inicio,c.vencimento)) and (c.vencimento<=coalesce(:fim,c.vencimento))
   and (:fornecedor is null or f.uuid=:fornecedor) and (:status is null or c.status=:status)
   and (:somenteVencidas=false or (coalesce(c.vencida,false) and c.status<>'CANCELADA' and c.saldo>0))
 """,nativeQuery=true)
 ResumoProjection resumo(@Param("org") UUID org,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim,@Param("fornecedor") UUID fornecedor,@Param("status") String status,@Param("somenteVencidas") boolean somenteVencidas);
}
