package br.com.admeclesial.domain.organizacao;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organizacao_eclesial", schema = "adm_eclesial")
public class OrganizacaoEclesial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(name = "nome_reduzido", length = 100)
    private String nomeReduzido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoOrganizacaoEclesial tipo;

    @Column(length = 14)
    private String cnpj;

    @Column(length = 200)
    private String email;

    @Column(length = 30)
    private String telefone;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(columnDefinition = "text")
    private String observacao;

    @PrePersist
    void prePersist() {
        if (uuid == null) uuid = UUID.randomUUID();
        if (dataCadastro == null) dataCadastro = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public UUID getUuid() { return uuid; }
    public String getNome() { return nome; }
    public String getNomeReduzido() { return nomeReduzido; }
    public TipoOrganizacaoEclesial getTipo() { return tipo; }
    public String getCnpj() { return cnpj; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public boolean isAtivo() { return ativo; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public String getObservacao() { return observacao; }

    public void setNome(String value) { this.nome = value; }
    public void setNomeReduzido(String value) { this.nomeReduzido = value; }
    public void setTipo(TipoOrganizacaoEclesial value) { this.tipo = value; }
    public void setCnpj(String value) { this.cnpj = value; }
    public void setEmail(String value) { this.email = value; }
    public void setTelefone(String value) { this.telefone = value; }
    public void setAtivo(boolean value) { this.ativo = value; }
    public void setObservacao(String value) { this.observacao = value; }
}
