package br.com.admeclesial.application.relatorio;
import br.com.admeclesial.domain.projeto.*; import java.time.LocalDate; import java.util.UUID;
public record RelatorioProjetoFiltro(UUID organizacaoUuid, TipoProjeto tipo, StatusProjeto status, LocalDate inicio, LocalDate fim, boolean somenteRecursoSemUtilizacao, int pagina, int tamanho) {}
