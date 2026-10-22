package br.com.admeclesial.domain.projeto;
import jakarta.persistence.*;
import java.math.BigDecimal; import java.util.UUID;
@Entity @Table(name="utilizacao_recurso",schema="adm_eclesial")
public class UtilizacaoRecurso {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(nullable=false,unique=true,updatable=false) UUID uuid;
 @Column(name="fonte_recurso_projeto_id",nullable=false) Long fonteRecursoProjetoId; @Column(name="movimentacao_financeira_id",nullable=false) Long movimentacaoFinanceiraId;
 @Column(nullable=false,precision=15,scale=2) BigDecimal valor; @Column(length=500) String descricao;
 @PrePersist void pre(){if(uuid==null)uuid=UUID.randomUUID();}
 public Long getId(){return id;} public UUID getUuid(){return uuid;} public Long getFonteRecursoProjetoId(){return fonteRecursoProjetoId;} public Long getMovimentacaoFinanceiraId(){return movimentacaoFinanceiraId;} public BigDecimal getValor(){return valor;} public String getDescricao(){return descricao;}
 public void setFonteRecursoProjetoId(Long v){fonteRecursoProjetoId=v;} public void setMovimentacaoFinanceiraId(Long v){movimentacaoFinanceiraId=v;} public void setValor(BigDecimal v){valor=v;} public void setDescricao(String v){descricao=v;}
}
