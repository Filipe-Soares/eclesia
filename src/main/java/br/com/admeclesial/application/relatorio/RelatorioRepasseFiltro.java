package br.com.admeclesial.application.relatorio;
import br.com.admeclesial.domain.financeiro.StatusObrigacaoRepasse; import java.time.LocalDate; import java.util.UUID;
public record RelatorioRepasseFiltro(UUID organizacaoUuid, LocalDate vencimentoInicio, LocalDate vencimentoFim, StatusObrigacaoRepasse status, boolean somenteVencidos, int pagina, int tamanho) {}
