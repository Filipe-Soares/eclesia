package br.com.admeclesial.application.usuario;

import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.domain.usuario.PerfilAcesso;
import br.com.admeclesial.domain.usuario.Usuario;
import br.com.admeclesial.domain.usuario.UsuarioPerfil;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import br.com.admeclesial.infrastructure.persistence.usuario.UsuarioOrganizacaoRepository;
import br.com.admeclesial.infrastructure.persistence.usuario.UsuarioPerfilRepository;
import br.com.admeclesial.infrastructure.persistence.usuario.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SegurancaOrganizacaoServiceTest {
    @Mock UsuarioRepository usuarios;
    @Mock UsuarioOrganizacaoRepository acessos;
    @Mock OrganizacaoEclesialRepository organizacoes;
    @Mock UsuarioPerfilRepository perfis;

    @AfterEach void limparContexto(){ SecurityContextHolder.clearContext(); }

    @Test
    void deveNegarOrganizacaoNaoVinculadaParaUsuarioComum() {
        UUID organizacaoB = UUID.randomUUID();
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(10L);
        when(usuario.isAtivo()).thenReturn(true);
        when(usuarios.findByLoginIgnoreCase("secretaria")).thenReturn(Optional.of(usuario));
        when(acessos.existsByUsuarioIdAndOrganizacaoUuid(10L, organizacaoB)).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("secretaria", "n/a", List.of(new SimpleGrantedAuthority("ROLE_SECRETARIA"))));

        var service = new SegurancaOrganizacaoService(usuarios, acessos, organizacoes, perfis);
        assertThrows(AccessDeniedException.class, () -> service.validarAcesso(organizacaoB));
    }

    @Test
    void devePermitirSomenteOrganizacaoExplicitamenteVinculadaParaUsuarioComum() {
        UUID organizacaoA = UUID.randomUUID();
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(10L);
        when(usuario.isAtivo()).thenReturn(true);
        when(usuarios.findByLoginIgnoreCase("financeiro")).thenReturn(Optional.of(usuario));
        when(acessos.existsByUsuarioIdAndOrganizacaoUuid(10L, organizacaoA)).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("financeiro", "n/a", List.of(new SimpleGrantedAuthority("ROLE_FINANCEIRO"))));

        var service = new SegurancaOrganizacaoService(usuarios, acessos, organizacoes, perfis);
        assertDoesNotThrow(() -> service.validarAcesso(organizacaoA));
    }

    @Test
    void devePermitirAcessoAQualquerOrganizacaoParaPerfilAdministradorPorAuthority() {
        UUID organizacaoQualquer = UUID.randomUUID();
        Usuario usuario = mock(Usuario.class);
        when(usuario.isAtivo()).thenReturn(true);
        when(usuarios.findByLoginIgnoreCase("admin")).thenReturn(Optional.of(usuario));

        OrganizacaoEclesial org = mock(OrganizacaoEclesial.class);
        when(organizacoes.findByUuid(organizacaoQualquer)).thenReturn(Optional.of(org));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "n/a", List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));

        var service = new SegurancaOrganizacaoService(usuarios, acessos, organizacoes, perfis);
        assertDoesNotThrow(() -> service.validarAcesso(organizacaoQualquer));
        assertTrue(service.podeAcessar(organizacaoQualquer));
    }

    @Test
    void devePermitirAcessoAQualquerOrganizacaoParaPerfilAdministradorPorPerfilRepository() {
        UUID organizacaoQualquer = UUID.randomUUID();
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(1L);
        when(usuario.isAtivo()).thenReturn(true);
        when(usuarios.findByLoginIgnoreCase("admin")).thenReturn(Optional.of(usuario));

        UsuarioPerfil up = mock(UsuarioPerfil.class);
        when(up.getPerfil()).thenReturn(PerfilAcesso.ADMINISTRADOR);
        when(perfis.findByUsuarioId(1L)).thenReturn(List.of(up));

        OrganizacaoEclesial org = mock(OrganizacaoEclesial.class);
        when(organizacoes.findByUuid(organizacaoQualquer)).thenReturn(Optional.of(org));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "n/a", List.of()));

        var service = new SegurancaOrganizacaoService(usuarios, acessos, organizacoes, perfis);
        assertDoesNotThrow(() -> service.validarAcesso(organizacaoQualquer));
        assertTrue(service.podeAcessar(organizacaoQualquer));
    }

    @Test
    void deveListarTodasAsOrganizacoesParaAdministrador() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.isAtivo()).thenReturn(true);
        when(usuarios.findByLoginIgnoreCase("admin")).thenReturn(Optional.of(usuario));

        UUID org1 = UUID.randomUUID();
        UUID org2 = UUID.randomUUID();
        OrganizacaoEclesial o1 = mock(OrganizacaoEclesial.class);
        OrganizacaoEclesial o2 = mock(OrganizacaoEclesial.class);
        when(o1.getUuid()).thenReturn(org1);
        when(o2.getUuid()).thenReturn(org2);
        when(organizacoes.findAllByOrderByNomeAsc()).thenReturn(List.of(o1, o2));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "n/a", List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));

        var service = new SegurancaOrganizacaoService(usuarios, acessos, organizacoes, perfis);
        Set<UUID> autorizadas = service.organizacoesAutorizadas();

        assertEquals(2, autorizadas.size());
        assertTrue(autorizadas.contains(org1));
        assertTrue(autorizadas.contains(org2));
    }
}
