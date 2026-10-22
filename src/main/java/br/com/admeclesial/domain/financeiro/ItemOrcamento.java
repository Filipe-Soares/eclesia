package br.com.admeclesial.domain.financeiro;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
@Entity @Table(name="item_orcamento", schema="adm_eclesial")
public class ItemOrcamento {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Column(nullable=false,unique=true,updatable=false) UUID uuid;
 @Column(name="orcamento_id",nullable=false) Long orcamentoId;
 @Column(name="categoria_financeira_id",nullable=false) Long categoriaFinanceiraId;
 @Column(name="centro_custo_id") Long centroCustoId;
 @Column(nullable=false,length=300) String descricao;
 @Column(name="valor_planejado",nullable=false,precision=15,scale=2) BigDecimal valorPlanejado;
 @PrePersist void pre(){if(uuid==null)uuid=UUID.randomUUID();}
 public Long getId(){return id;} public UUID getUuid(){return uuid;} public Long getOrcamentoId(){return orcamentoId;} public Long getCategoriaFinanceiraId(){return categoriaFinanceiraId;} public Long getCentroCustoId(){return centroCustoId;} public String getDescricao(){return descricao;} public BigDecimal getValorPlanejado(){return valorPlanejado;}
 public void setOrcamentoId(Long v){orcamentoId=v;} public void setCategoriaFinanceiraId(Long v){categoriaFinanceiraId=v;} public void setCentroCustoId(Long v){centroCustoId=v;} public void setDescricao(String v){descricao=v;} public void setValorPlanejado(BigDecimal v){valorPlanejado=v;}
}
