package br.com.admeclesial.application.usuario;

import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.domain.usuario.PerfilAcesso;
import br.com.admeclesial.domain.usuario.Usuario;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import br.com.admeclesial.infrastructure.persistence.usuario.UsuarioOrganizacaoRepository;
import br.com.admeclesial.infrastructure.persistence.usuario.UsuarioPerfilRepository;
import br.com.admeclesial.infrastructure.persistence.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SegurancaOrganizacaoService {
    private final UsuarioRepository usuarios;
    private final UsuarioOrganizacaoRepository acessos;
    private final OrganizacaoEclesialRepository organizacoes;
    private final UsuarioPerfilRepository perfis;

    @Autowired
    public SegurancaOrganizacaoService(
            UsuarioRepository usuarios,
            UsuarioOrganizacaoRepository acessos,
            OrganizacaoEclesialRepository organizacoes,
            @Autowired(required = false) UsuarioPerfilRepository perfis) {
        this.usuarios = usuarios;
        this.acessos = acessos;
        this.organizacoes = organizacoes;
        this.perfis = perfis;
    }

    public SegurancaOrganizacaoService(UsuarioRepository u, UsuarioOrganizacaoRepository a) {
        this(u, a, null, null);
    }

    @Transactional(readOnly = true)
    public boolean podeAcessar(UUID organizacaoUuid) {
        if (organizacaoUuid == null) return false;
        var usuario = usuarioAtual();
        if (usuario == null) return false;
        if (isAdministrador(usuario)) {
            return organizacoes == null || organizacoes.findByUuid(organizacaoUuid).isPresent();
        }
        return acessos.existsByUsuarioIdAndOrganizacaoUuid(usuario.getId(), organizacaoUuid);
    }

    @Transactional(readOnly = true)
    public Set<UUID> organizacoesAutorizadas() {
        var usuario = usuarioAtual();
        if (usuario == null) return Set.of();
        if (isAdministrador(usuario) && organizacoes != null) {
            return organizacoes.findAllByOrderByNomeAsc().stream()
                    .map(OrganizacaoEclesial::getUuid)
                    .collect(Collectors.toUnmodifiableSet());
        }
        return acessos.findByUsuarioId(usuario.getId()).stream()
                .map(a -> a.getOrganizacao().getUuid())
                .collect(Collectors.toUnmodifiableSet());
    }

    public void validarAcesso(UUID uuid) {
        if (!podeAcessar(uuid)) throw new AccessDeniedException("Usuário sem acesso à organização informada");
    }

    @Transactional(readOnly = true)
    public boolean ehAdministrador() {
        return isAdministrador(usuarioAtual());
    }

    @Transactional(readOnly = true)
    public void validarAcessoId(Long organizacaoId) {
        if (organizacaoId == null) throw new AccessDeniedException("Organização não informada");
        if (organizacoes == null) throw new AccessDeniedException("Repositório de organizações indisponível");
        var uuid = organizacoes.findById(organizacaoId)
                .map(OrganizacaoEclesial::getUuid)
                .orElseThrow(() -> new AccessDeniedException("Organização não encontrada"));
        validarAcesso(uuid);
    }

    private boolean isAdministrador(Usuario usuario) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            boolean hasAdminAuthority = auth.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMINISTRADOR".equalsIgnoreCase(a.getAuthority())
                            || "ADMINISTRADOR".equalsIgnoreCase(a.getAuthority()));
            if (hasAdminAuthority) return true;
        }
        if (usuario != null && perfis != null) {
            return perfis.findByUsuarioId(usuario.getId()).stream()
                    .anyMatch(p -> p.getPerfil() == PerfilAcesso.ADMINISTRADOR);
        }
        return false;
    }

    private Usuario usuarioAtual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) return null;
        var usuario = usuarios.findByLoginIgnoreCase(auth.getName()).orElse(null);
        return usuario != null && usuario.isAtivo() ? usuario : null;
    }
}
