package br.com.admeclesial.domain.financeiro;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
@Entity @Table(name="orcamento", schema="adm_eclesial")
public class Orcamento {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Column(nullable=false,unique=true,updatable=false) UUID uuid;
 @Column(name="organizacao_eclesial_id",nullable=false) Long organizacaoEclesialId;
 @Column(nullable=false) Integer ano;
 @Column(nullable=false,length=200) String nome;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) StatusOrcamento status;
 @Column(name="data_aprovacao") LocalDate dataAprovacao;
 @Column(columnDefinition="text") String observacao;
 @PrePersist void pre(){if(uuid==null)uuid=UUID.randomUUID();}
 public Long getId(){return id;} public UUID getUuid(){return uuid;} public Long getOrganizacaoEclesialId(){return organizacaoEclesialId;} public Integer getAno(){return ano;} public String getNome(){return nome;} public StatusOrcamento getStatus(){return status;} public LocalDate getDataAprovacao(){return dataAprovacao;} public String getObservacao(){return observacao;}
 public void setOrganizacaoEclesialId(Long v){organizacaoEclesialId=v;} public void setAno(Integer v){ano=v;} public void setNome(String v){nome=v;} public void setStatus(StatusOrcamento v){status=v;} public void setDataAprovacao(LocalDate v){dataAprovacao=v;} public void setObservacao(String v){observacao=v;}
}
