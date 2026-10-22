package br.com.admeclesial.application.relatorio;
import java.util.UUID;
public record RelatorioFiltroBase(UUID organizacaoUuid,int pagina,int tamanho,String ordenacao,String direcao) {
 public int paginaSegura(){return Math.max(pagina,0);} public int tamanhoSeguro(){return Math.min(Math.max(tamanho,10),100);} public boolean descendente(){return "desc".equalsIgnoreCase(direcao);}
}
