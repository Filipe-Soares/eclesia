package br.com.admeclesial.application.catequese;
import java.util.UUID;
public record EtapaCatequeseResponse(UUID uuid,UUID catequeseUuid,String catequeseNome,String nome,String descricao,Integer ordem,boolean ativo) {}
