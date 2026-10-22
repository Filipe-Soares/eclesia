package br.com.admeclesial.application.relatorio;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record RelatorioMovimentacaoItem(UUID uuid, LocalDate data, String tipo, String conta, String categoria, String centroCusto, String descricao, BigDecimal valor, String status) {}
