package br.com.admeclesial.application.relatorio;
import java.util.List;
public record RelatorioPagina<T>(List<T> itens,int pagina,int tamanho,long totalElementos,int totalPaginas,String ordenacao,String direcao) {}
