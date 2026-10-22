package br.com.admeclesial.infrastructure.persistence.relatorio;
import java.time.LocalDate; import java.util.UUID;
public interface RelatorioSacramentoRegistroProjection { UUID getUuid(); String getTipo(); LocalDate getDataCelebracao(); String getPessoaPrincipal(); String getLocal(); String getStatus(); String getLivro(); String getFolha(); String getTermo(); String getNumeroRegistro(); }
