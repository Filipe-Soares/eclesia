package br.com.admeclesial.domain.catequese;

import br.com.admeclesial.domain.pessoa.Pessoa;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "catequista_turma", schema = "adm_eclesial")
public class CatequistaTurma {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, updatable = false) private UUID uuid;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "turma_catequese_id", nullable = false) private TurmaCatequese turmaCatequese;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "pessoa_id", nullable = false) private Pessoa pessoa;
    @Column(name = "data_inicio", nullable = false) private LocalDate dataInicio;
    @Column(name = "data_fim") private LocalDate dataFim;
    @Column(nullable = false) private boolean principal;
    @Column(columnDefinition = "text") private String observacao;
    @PrePersist void prePersist(){if(uuid==null)uuid=UUID.randomUUID();}
    public Long getId(){return id;} public UUID getUuid(){return uuid;} public TurmaCatequese getTurmaCatequese(){return turmaCatequese;} public Pessoa getPessoa(){return pessoa;} public LocalDate getDataInicio(){return dataInicio;} public LocalDate getDataFim(){return dataFim;} public boolean isPrincipal(){return principal;} public String getObservacao(){return observacao;}
    public void setTurmaCatequese(TurmaCatequese v){turmaCatequese=v;} public void setPessoa(Pessoa v){pessoa=v;} public void setDataInicio(LocalDate v){dataInicio=v;} public void setDataFim(LocalDate v){dataFim=v;} public void setPrincipal(boolean v){principal=v;} public void setObservacao(String v){observacao=v;}
}
