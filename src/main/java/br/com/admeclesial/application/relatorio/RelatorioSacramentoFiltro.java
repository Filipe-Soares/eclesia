package br.com.admeclesial.application.relatorio;
import br.com.admeclesial.domain.sacramento.TipoSacramento; import java.time.LocalDate; import java.util.UUID;
public record RelatorioSacramentoFiltro(UUID organizacaoUuid,TipoSacramento tipo,LocalDate dataInicio,LocalDate dataFim,String pessoa,int pagina,int tamanho) {}
