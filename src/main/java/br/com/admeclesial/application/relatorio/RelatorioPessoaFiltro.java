package br.com.admeclesial.application.relatorio;
import java.util.UUID;
public record RelatorioPessoaFiltro(UUID organizacaoUuid,String nome,Boolean ativo,int pagina,int tamanho,String ordenacao,String direcao) {}
