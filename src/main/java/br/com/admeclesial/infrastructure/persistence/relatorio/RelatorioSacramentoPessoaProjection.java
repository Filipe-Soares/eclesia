package br.com.admeclesial.infrastructure.persistence.relatorio;
import java.time.LocalDate; import java.util.UUID;
public interface RelatorioSacramentoPessoaProjection { UUID getPessoaUuid(); String getPessoa(); String getTipo(); LocalDate getDataCelebracao(); UUID getSacramentoUuid(); String getStatus(); }
