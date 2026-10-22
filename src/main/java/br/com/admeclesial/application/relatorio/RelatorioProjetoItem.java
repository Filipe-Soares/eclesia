package br.com.admeclesial.application.relatorio;
import br.com.admeclesial.domain.projeto.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record RelatorioProjetoItem(UUID uuid,String nome,TipoProjeto tipo,StatusProjeto status,LocalDate dataInicio,LocalDate dataFim,BigDecimal valorEstimado,BigDecimal recursosVinculados,BigDecimal recursosUtilizados,BigDecimal saldoDisponivel,boolean recursoSemUtilizacao,boolean exibirFinanceiro) {}
