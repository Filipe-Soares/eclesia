package br.com.admeclesial.domain.sacramento;
import br.com.admeclesial.domain.pessoa.Pessoa;
import jakarta.persistence.*; import java.util.UUID;
@Entity @Table(name="participante_sacramento", schema="adm_eclesial")
public class ParticipanteSacramento {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,updatable=false) private UUID uuid;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="sacramento_id",nullable=false) private Sacramento sacramento;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="pessoa_id",nullable=false) private Pessoa pessoa;
 @Enumerated(EnumType.STRING) @Column(name="tipo_participacao",nullable=false,length=50) private TipoParticipacaoSacramento tipoParticipacao;
 @Column(columnDefinition="text") private String observacao;
 @PrePersist void prePersist(){if(uuid==null)uuid=UUID.randomUUID();}
 public Long getId(){return id;} public UUID getUuid(){return uuid;} public Sacramento getSacramento(){return sacramento;} public Pessoa getPessoa(){return pessoa;} public TipoParticipacaoSacramento getTipoParticipacao(){return tipoParticipacao;} public String getObservacao(){return observacao;}
 public void setSacramento(Sacramento v){sacramento=v;} public void setPessoa(Pessoa v){pessoa=v;} public void setTipoParticipacao(TipoParticipacaoSacramento v){tipoParticipacao=v;} public void setObservacao(String v){observacao=v;}
}
