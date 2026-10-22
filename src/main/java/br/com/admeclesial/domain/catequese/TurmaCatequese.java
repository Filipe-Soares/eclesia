package br.com.admeclesial.domain.catequese;

import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "turma_catequese", schema = "adm_eclesial")
public class TurmaCatequese {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, updatable = false) private UUID uuid;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "etapa_catequese_id", nullable = false) private EtapaCatequese etapaCatequese;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organizacao_eclesial_id", nullable = false) private OrganizacaoEclesial organizacaoEclesial;
    @Column(nullable = false, length = 150) private String nome;
    private Integer ano;
    @Enumerated(EnumType.STRING) @Column(length = 30) private TurnoCatequese turno;
    @Column(name = "data_inicio") private LocalDate dataInicio;
    @Column(name = "data_fim") private LocalDate dataFim;
    private Integer capacidade;
    @Column(length = 255) private String local;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private StatusCatequese status = StatusCatequese.PLANEJADA;
    @Column(columnDefinition = "text") private String observacao;
    @PrePersist void prePersist(){if(uuid==null)uuid=UUID.randomUUID();}
    public Long getId(){return id;} public UUID getUuid(){return uuid;} public EtapaCatequese getEtapaCatequese(){return etapaCatequese;} public OrganizacaoEclesial getOrganizacaoEclesial(){return organizacaoEclesial;} public String getNome(){return nome;} public Integer getAno(){return ano;} public TurnoCatequese getTurno(){return turno;} public LocalDate getDataInicio(){return dataInicio;} public LocalDate getDataFim(){return dataFim;} public Integer getCapacidade(){return capacidade;} public String getLocal(){return local;} public StatusCatequese getStatus(){return status;} public String getObservacao(){return observacao;}
    public void setEtapaCatequese(EtapaCatequese v){etapaCatequese=v;} public void setOrganizacaoEclesial(OrganizacaoEclesial v){organizacaoEclesial=v;} public void setNome(String v){nome=v;} public void setAno(Integer v){ano=v;} public void setTurno(TurnoCatequese v){turno=v;} public void setDataInicio(LocalDate v){dataInicio=v;} public void setDataFim(LocalDate v){dataFim=v;} public void setCapacidade(Integer v){capacidade=v;} public void setLocal(String v){local=v;} public void setStatus(StatusCatequese v){status=v;} public void setObservacao(String v){observacao=v;}
}
