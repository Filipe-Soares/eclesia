package br.com.admeclesial.application.relatorio;
import java.util.UUID;
public record RelatorioPessoaItem(UUID uuid,String nome,String nomeSocial,String cpf,String email,boolean ativo,String organizacao) {}
