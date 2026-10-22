package br.com.admeclesial.infrastructure.persistence.relatorio;
import java.util.UUID;
public interface RelatorioTurmaProjection { UUID getUuid(); String getTurma(); String getCatequese(); String getEtapa(); Integer getAno(); String getTurno(); String getStatus(); Integer getCapacidade(); Long getInscritos(); Long getVagas(); String getCatequistas(); }
