package br.com.admeclesial.domain.organizacao;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "funcao_eclesial", schema = "adm_eclesial")
public class FuncaoEclesial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(columnDefinition = "text")
    private String descricao;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "exclusivo_na_organizacao", nullable = false)
    private boolean exclusivoNaOrganizacao;

    @PrePersist
    void prePersist() {
        if (uuid == null) uuid = UUID.randomUUID();
    }

    public Long getId() { return id; }
    public UUID getUuid() { return uuid; }
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public boolean isAtiva() { return ativo; }
    public boolean isExclusivoNaOrganizacao() { return exclusivoNaOrganizacao; }

    public void setCodigo(String value) { this.codigo = value; }
    public void setNome(String value) { this.nome = value; }
    public void setDescricao(String value) { this.descricao = value; }
    public void setAtiva(boolean value) { this.ativo = value; }
    public void setExclusivoNaOrganizacao(boolean value) { this.exclusivoNaOrganizacao = value; }
}
