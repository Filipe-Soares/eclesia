package br.com.admeclesial.domain.catequese;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "etapa_catequese", schema = "adm_eclesial")
public class EtapaCatequese {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, updatable = false) private UUID uuid;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "catequese_id", nullable = false) private Catequese catequese;
    @Column(nullable = false, length = 150) private String nome;
    @Column(columnDefinition = "text") private String descricao;
    @Column(nullable = false) private Integer ordem;
    @Column(nullable = false) private boolean ativo = true;
    @PrePersist void prePersist(){if(uuid==null)uuid=UUID.randomUUID();}
    public Long getId(){return id;} public UUID getUuid(){return uuid;} public Catequese getCatequese(){return catequese;} public String getNome(){return nome;} public String getDescricao(){return descricao;} public Integer getOrdem(){return ordem;} public boolean isAtivo(){return ativo;}
    public void setCatequese(Catequese v){catequese=v;} public void setNome(String v){nome=v;} public void setDescricao(String v){descricao=v;} public void setOrdem(Integer v){ordem=v;} public void setAtivo(boolean v){ativo=v;}
}
