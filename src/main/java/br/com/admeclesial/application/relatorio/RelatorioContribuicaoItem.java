package br.com.admeclesial.application.relatorio;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record RelatorioContribuicaoItem(UUID uuid, LocalDate data, String tipo, String categoria, String contribuinte, BigDecimal valor, String meioRecebimento, String destinacao) {}
