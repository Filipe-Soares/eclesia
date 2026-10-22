package br.com.admeclesial.infrastructure.persistence.usuario;
import br.com.admeclesial.domain.usuario.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface UsuarioPerfilRepository extends JpaRepository<UsuarioPerfil,Long>{ List<UsuarioPerfil> findByUsuarioId(Long usuarioId); boolean existsByUsuarioIdAndPerfil(Long usuarioId,PerfilAcesso perfil); void deleteByUsuarioId(Long usuarioId); }
