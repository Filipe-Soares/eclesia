package br.com.admeclesial.application.usuario;
import br.com.admeclesial.domain.usuario.PerfilAcesso; import java.util.*;
public record UsuarioResponse(UUID uuid,UUID pessoaUuid,String pessoaNome,String login,boolean ativo,Set<PerfilAcesso> perfis,Set<UUID> organizacoes){}
