package br.com.admeclesial.application.relatorio;
import br.com.admeclesial.domain.financeiro.TipoMovimentacaoFinanceira; import java.time.LocalDate; import java.util.UUID;
public record RelatorioMovimentacaoFiltro(UUID organizacaoUuid, LocalDate dataInicio, LocalDate dataFim, UUID contaUuid, TipoMovimentacaoFinanceira tipo, UUID categoriaUuid, UUID centroCustoUuid, int pagina, int tamanho) {}
