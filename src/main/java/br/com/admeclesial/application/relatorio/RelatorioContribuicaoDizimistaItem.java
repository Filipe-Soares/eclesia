package br.com.admeclesial.application.relatorio;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record RelatorioContribuicaoDizimistaItem(UUID pessoaUuid, String pessoa, long quantidade, BigDecimal valorTotal, LocalDate ultimaContribuicao) {}
