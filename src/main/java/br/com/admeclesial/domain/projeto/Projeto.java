package br.com.admeclesial.domain.projeto;
import jakarta.persistence.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name="projeto",schema="adm_eclesial")
public class Projeto {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Column(nullable=false,unique=true,updatable=false) UUID uuid;
 @Column(name="organizacao_eclesial_id",nullable=false) Long organizacaoEclesialId;
 @Column(name="item_orcamento_id") Long itemOrcamentoId;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) TipoProjeto tipo;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) StatusProjeto status;
 @Column(nullable=false,length=200) String nome;
 @Column(columnDefinition="text") String descricao;
 @Column(name="data_inicio") LocalDate dataInicio;
 @Column(name="data_fim") LocalDate dataFim;
 @Column(name="valor_estimado",precision=15,scale=2) BigDecimal valorEstimado;
 @PrePersist void pre(){if(uuid==null)uuid=UUID.randomUUID();}
 public Long getId(){return id;} public UUID getUuid(){return uuid;} public Long getOrganizacaoEclesialId(){return organizacaoEclesialId;} public Long getItemOrcamentoId(){return itemOrcamentoId;} public TipoProjeto getTipo(){return tipo;} public StatusProjeto getStatus(){return status;} public String getNome(){return nome;} public String getDescricao(){return descricao;} public LocalDate getDataInicio(){return dataInicio;} public LocalDate getDataFim(){return dataFim;} public BigDecimal getValorEstimado(){return valorEstimado;}
 public void setOrganizacaoEclesialId(Long v){organizacaoEclesialId=v;} public void setItemOrcamentoId(Long v){itemOrcamentoId=v;} public void setTipo(TipoProjeto v){tipo=v;} public void setStatus(StatusProjeto v){status=v;} public void setNome(String v){nome=v;} public void setDescricao(String v){descricao=v;} public void setDataInicio(LocalDate v){dataInicio=v;} public void setDataFim(LocalDate v){dataFim=v;} public void setValorEstimado(BigDecimal v){valorEstimado=v;}
}
