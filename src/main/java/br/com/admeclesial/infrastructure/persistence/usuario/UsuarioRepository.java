package br.com.admeclesial.infrastructure.persistence.usuario;
import br.com.admeclesial.domain.usuario.Usuario; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{ Optional<Usuario> findByLoginIgnoreCase(String login); Optional<Usuario> findByUuid(UUID uuid); boolean existsByLoginIgnoreCase(String login); List<Usuario> findAllByOrderByLoginAsc(); }
