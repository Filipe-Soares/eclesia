package br.com.admeclesial.infrastructure.persistence.usuario;
import br.com.admeclesial.domain.usuario.UsuarioOrganizacao; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface UsuarioOrganizacaoRepository extends JpaRepository<UsuarioOrganizacao,Long>{ List<UsuarioOrganizacao> findByUsuarioId(Long usuarioId); boolean existsByUsuarioIdAndOrganizacaoUuid(Long usuarioId,UUID uuid); void deleteByUsuarioId(Long usuarioId); }
