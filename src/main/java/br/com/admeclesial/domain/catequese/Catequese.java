package br.com.admeclesial.domain.catequese;

import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "catequese", schema = "adm_eclesial")
public class Catequese {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, updatable = false) private UUID uuid;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_eclesial_id", nullable = false) private OrganizacaoEclesial organizacaoEclesial;
    @Column(nullable = false, length = 200) private String nome;
    @Column(columnDefinition = "text") private String descricao;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private StatusCatequese status = StatusCatequese.PLANEJADA;
    @Column(name = "data_inicio") private LocalDate dataInicio;
    @Column(name = "data_fim") private LocalDate dataFim;
    @Column(columnDefinition = "text") private String observacao;

    @PrePersist void prePersist() { if (uuid == null) uuid = UUID.randomUUID(); }
    public Long getId(){return id;} public UUID getUuid(){return uuid;} public OrganizacaoEclesial getOrganizacaoEclesial(){return organizacaoEclesial;}
    public String getNome(){return nome;} public String getDescricao(){return descricao;} public StatusCatequese getStatus(){return status;}
    public LocalDate getDataInicio(){return dataInicio;} public LocalDate getDataFim(){return dataFim;} public String getObservacao(){return observacao;}
    public void setOrganizacaoEclesial(OrganizacaoEclesial v){organizacaoEclesial=v;} public void setNome(String v){nome=v;} public void setDescricao(String v){descricao=v;}
    public void setStatus(StatusCatequese v){status=v;} public void setDataInicio(LocalDate v){dataInicio=v;} public void setDataFim(LocalDate v){dataFim=v;} public void setObservacao(String v){observacao=v;}
}
