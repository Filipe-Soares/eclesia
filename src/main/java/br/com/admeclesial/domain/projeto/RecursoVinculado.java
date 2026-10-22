package br.com.admeclesial.domain.projeto;
import jakarta.persistence.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name="recurso_vinculado",schema="adm_eclesial")
public class RecursoVinculado {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(nullable=false,unique=true,updatable=false) UUID uuid;
 @Column(name="organizacao_eclesial_id",nullable=false) Long organizacaoEclesialId;
 @Column(name="destinacao_contribuicao_id",nullable=false,unique=true) Long destinacaoContribuicaoId;
 @Column(nullable=false,length=500) String finalidade;
 @Column(name="valor_original",nullable=false,precision=15,scale=2) BigDecimal valorOriginal;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) StatusRecursoVinculado status;
 @Column(name="data_criacao",nullable=false) LocalDate dataCriacao; @Column(columnDefinition="text") String observacao;
 @PrePersist void pre(){if(uuid==null)uuid=UUID.randomUUID(); if(dataCriacao==null)dataCriacao=LocalDate.now();}
 public Long getId(){return id;} public UUID getUuid(){return uuid;} public Long getOrganizacaoEclesialId(){return organizacaoEclesialId;} public Long getDestinacaoContribuicaoId(){return destinacaoContribuicaoId;} public String getFinalidade(){return finalidade;} public BigDecimal getValorOriginal(){return valorOriginal;} public StatusRecursoVinculado getStatus(){return status;} public LocalDate getDataCriacao(){return dataCriacao;} public String getObservacao(){return observacao;}
 public void setOrganizacaoEclesialId(Long v){organizacaoEclesialId=v;} public void setDestinacaoContribuicaoId(Long v){destinacaoContribuicaoId=v;} public void setFinalidade(String v){finalidade=v;} public void setValorOriginal(BigDecimal v){valorOriginal=v;} public void setStatus(StatusRecursoVinculado v){status=v;} public void setDataCriacao(LocalDate v){dataCriacao=v;} public void setObservacao(String v){observacao=v;}
}
