package br.com.admeclesial.domain.sacramento;
import jakarta.persistence.*; import java.util.UUID;
@Entity @Table(name="registro_sacramental",schema="adm_eclesial")
public class RegistroSacramental {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,updatable=false) private UUID uuid;
 @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="sacramento_id",nullable=false,unique=true) private Sacramento sacramento;
 @Column(length=50) private String livro; @Column(length=50) private String folha; @Column(length=50) private String termo; @Column(name="numero_registro",length=50) private String numeroRegistro;
 @Column(columnDefinition="text") private String observacao;
 @PrePersist void prePersist(){if(uuid==null)uuid=UUID.randomUUID();}
 public Long getId(){return id;} public UUID getUuid(){return uuid;} public Sacramento getSacramento(){return sacramento;} public String getLivro(){return livro;} public String getFolha(){return folha;} public String getTermo(){return termo;} public String getNumeroRegistro(){return numeroRegistro;} public String getObservacao(){return observacao;}
 public void setSacramento(Sacramento v){sacramento=v;} public void setLivro(String v){livro=v;} public void setFolha(String v){folha=v;} public void setTermo(String v){termo=v;} public void setNumeroRegistro(String v){numeroRegistro=v;} public void setObservacao(String v){observacao=v;}
}
