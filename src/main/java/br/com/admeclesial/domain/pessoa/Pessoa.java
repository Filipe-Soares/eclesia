package br.com.admeclesial.domain.pessoa;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pessoa", schema = "adm_eclesial")
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(name = "nome_social", length = 200)
    private String nomeSocial;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoSexo sexo;

    @Column(length = 11)
    private String cpf;

    @Column(length = 30)
    private String rg;

    @Column(length = 200)
    private String email;

    @Column(columnDefinition = "text")
    private String observacao;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    void prePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public UUID getUuid() { return uuid; }
    public String getNome() { return nome; }
    public String getNomeSocial() { return nomeSocial; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public TipoSexo getSexo() { return sexo; }
    public String getCpf() { return cpf; }
    public String getRg() { return rg; }
    public String getEmail() { return email; }
    public String getObservacao() { return observacao; }
    public boolean isAtivo() { return ativo; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }

    public void setNome(String nome) { this.nome = nome; }
    public void setNomeSocial(String nomeSocial) { this.nomeSocial = nomeSocial; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public void setSexo(TipoSexo sexo) { this.sexo = sexo; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setRg(String rg) { this.rg = rg; }
    public void setEmail(String email) { this.email = email; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
