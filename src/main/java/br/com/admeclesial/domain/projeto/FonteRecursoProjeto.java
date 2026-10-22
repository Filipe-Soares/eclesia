package br.com.admeclesial.domain.projeto;
import jakarta.persistence.*;
import java.math.BigDecimal; import java.util.UUID;
@Entity @Table(name="fonte_recurso_projeto",schema="adm_eclesial")
public class FonteRecursoProjeto {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(nullable=false,unique=true,updatable=false) UUID uuid;
 @Column(name="projeto_id",nullable=false) Long projetoId; @Column(name="recurso_vinculado_id",nullable=false) Long recursoVinculadoId;
 @Column(name="valor_alocado",nullable=false,precision=15,scale=2) BigDecimal valorAlocado; @Column(length=500) String descricao;
 @PrePersist void pre(){if(uuid==null)uuid=UUID.randomUUID();}
 public Long getId(){return id;} public UUID getUuid(){return uuid;} public Long getProjetoId(){return projetoId;} public Long getRecursoVinculadoId(){return recursoVinculadoId;} public BigDecimal getValorAlocado(){return valorAlocado;} public String getDescricao(){return descricao;}
 public void setProjetoId(Long v){projetoId=v;} public void setRecursoVinculadoId(Long v){recursoVinculadoId=v;} public void setValorAlocado(BigDecimal v){valorAlocado=v;} public void setDescricao(String v){descricao=v;}
}
