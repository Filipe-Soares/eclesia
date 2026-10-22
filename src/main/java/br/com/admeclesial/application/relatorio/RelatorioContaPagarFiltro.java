package br.com.admeclesial.application.relatorio;
import br.com.admeclesial.domain.financeiro.StatusContaAPagar; import java.time.LocalDate; import java.util.UUID;
public record RelatorioContaPagarFiltro(UUID organizacaoUuid, LocalDate vencimentoInicio, LocalDate vencimentoFim, UUID fornecedorUuid, StatusContaAPagar status, boolean somenteVencidas, int pagina, int tamanho) {}
