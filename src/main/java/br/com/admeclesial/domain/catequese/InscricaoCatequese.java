package br.com.admeclesial.domain.catequese;

import br.com.admeclesial.domain.pessoa.Pessoa;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "inscricao_catequese", schema = "adm_eclesial")
public class InscricaoCatequese {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, updatable = false) private UUID uuid;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "turma_catequese_id", nullable = false) private TurmaCatequese turmaCatequese;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "pessoa_id", nullable = false) private Pessoa pessoa;
    @Column(name = "data_inscricao", nullable = false) private LocalDate dataInscricao;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private StatusInscricaoCatequese status = StatusInscricaoCatequese.ATIVA;
    @Column(columnDefinition = "text") private String observacao;
    @PrePersist void prePersist(){if(uuid==null)uuid=UUID.randomUUID(); if(dataInscricao==null)dataInscricao=LocalDate.now();}
    public Long getId(){return id;} public UUID getUuid(){return uuid;} public TurmaCatequese getTurmaCatequese(){return turmaCatequese;} public Pessoa getPessoa(){return pessoa;} public LocalDate getDataInscricao(){return dataInscricao;} public StatusInscricaoCatequese getStatus(){return status;} public String getObservacao(){return observacao;}
    public void setTurmaCatequese(TurmaCatequese v){turmaCatequese=v;} public void setPessoa(Pessoa v){pessoa=v;} public void setDataInscricao(LocalDate v){dataInscricao=v;} public void setStatus(StatusInscricaoCatequese v){status=v;} public void setObservacao(String v){observacao=v;}
}
