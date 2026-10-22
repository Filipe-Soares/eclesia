package br.com.admeclesial.application.relatorio;
import br.com.admeclesial.domain.financeiro.TipoContribuicao; import java.time.LocalDate; import java.util.UUID;
public record RelatorioContribuicaoFiltro(UUID organizacaoUuid, LocalDate dataInicio, LocalDate dataFim, TipoContribuicao tipo, UUID categoriaUuid, String pessoa, int pagina, int tamanho) {}
