package br.com.admeclesial.application.usuario;
import br.com.admeclesial.domain.usuario.PerfilAcesso; import jakarta.validation.constraints.*; import java.util.*;
public record UsuarioRequest(@NotNull UUID pessoaUuid,@NotBlank String login,String senha,boolean ativo,@NotEmpty Set<PerfilAcesso> perfis,@NotEmpty Set<UUID> organizacoes){}
