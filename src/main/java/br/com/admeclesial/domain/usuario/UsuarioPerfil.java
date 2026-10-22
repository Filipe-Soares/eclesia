package br.com.admeclesial.domain.usuario;
import jakarta.persistence.*;
@Entity @Table(name="usuario_perfil",schema="adm_eclesial",uniqueConstraints=@UniqueConstraint(name="uq_usuario_perfil",columnNames={"usuario_id","perfil"}))
public class UsuarioPerfil { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="usuario_id") private Usuario usuario; @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private PerfilAcesso perfil; public Long getId(){return id;} public Usuario getUsuario(){return usuario;} public PerfilAcesso getPerfil(){return perfil;} public void setUsuario(Usuario v){usuario=v;} public void setPerfil(PerfilAcesso v){perfil=v;} }
