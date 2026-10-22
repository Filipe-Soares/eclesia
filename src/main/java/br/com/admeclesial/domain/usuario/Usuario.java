package br.com.admeclesial.domain.usuario;
import br.com.admeclesial.domain.pessoa.Pessoa;
import jakarta.persistence.*;
import java.util.*;
@Entity @Table(name="usuario", schema="adm_eclesial")
public class Usuario {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,updatable=false) private UUID uuid;
 @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="pessoa_id",nullable=false,unique=true) private Pessoa pessoa;
 @Column(nullable=false,unique=true,length=100) private String login;
 @Column(name="senha_hash",nullable=false,length=255) private String senhaHash;
 @Column(nullable=false) private boolean ativo=true;
 @PrePersist void prePersist(){if(uuid==null)uuid=UUID.randomUUID();}
 public Long getId(){return id;} public UUID getUuid(){return uuid;} public Pessoa getPessoa(){return pessoa;} public String getLogin(){return login;} public String getSenhaHash(){return senhaHash;} public boolean isAtivo(){return ativo;}
 public void setPessoa(Pessoa v){pessoa=v;} public void setLogin(String v){login=v;} public void setSenhaHash(String v){senhaHash=v;} public void setAtivo(boolean v){ativo=v;}
}
