package br.com.admeclesial.domain.sacramento;
import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import jakarta.persistence.*;
import java.time.LocalDate; import java.time.LocalDateTime; import java.util.UUID;
@Entity @Table(name="sacramento", schema="adm_eclesial")
public class Sacramento {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,updatable=false) private UUID uuid;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="organizacao_eclesial_id",nullable=false) private OrganizacaoEclesial organizacaoEclesial;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private TipoSacramento tipo;
 @Column(name="data_celebracao",nullable=false) private LocalDate dataCelebracao;
 @Column(length=200) private String local;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private StatusSacramento status=StatusSacramento.REGISTRADO;
 @Column(columnDefinition="text") private String observacao;
 @Column(name="data_registro",nullable=false) private LocalDateTime dataRegistro;
 @PrePersist void prePersist(){if(uuid==null)uuid=UUID.randomUUID(); if(dataRegistro==null)dataRegistro=LocalDateTime.now();}
 public Long getId(){return id;} public UUID getUuid(){return uuid;} public OrganizacaoEclesial getOrganizacaoEclesial(){return organizacaoEclesial;} public TipoSacramento getTipo(){return tipo;} public LocalDate getDataCelebracao(){return dataCelebracao;} public String getLocal(){return local;} public StatusSacramento getStatus(){return status;} public String getObservacao(){return observacao;} public LocalDateTime getDataRegistro(){return dataRegistro;}
 public void setOrganizacaoEclesial(OrganizacaoEclesial v){organizacaoEclesial=v;} public void setTipo(TipoSacramento v){tipo=v;} public void setDataCelebracao(LocalDate v){dataCelebracao=v;} public void setLocal(String v){local=v;} public void setStatus(StatusSacramento v){status=v;} public void setObservacao(String v){observacao=v;}
}
