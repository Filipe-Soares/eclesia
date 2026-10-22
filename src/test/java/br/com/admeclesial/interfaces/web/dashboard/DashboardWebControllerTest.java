package br.com.admeclesial.interfaces.web.dashboard;

import br.com.admeclesial.application.dashboard.DashboardApplicationService;
import br.com.admeclesial.application.relatorio.RelatorioContextoService;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class DashboardWebControllerTest {
    private final DashboardWebController controller = new DashboardWebController(
            mock(DashboardApplicationService.class),
            mock(RelatorioContextoService.class),
            mock(SegurancaOrganizacaoService.class));

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void administradorTemPrecedenciaSobreDemaisPerfis() {
        autenticar("ROLE_CONSULTA", "ROLE_CATEQUESE", "ROLE_SECRETARIA", "ROLE_FINANCEIRO", "ROLE_ADMINISTRADOR");
        assertEquals(DashboardWebController.PerfilDashboard.ADMINISTRADOR, controller.perfilDashboard());
    }

    @Test
    void financeiroTemPrecedenciaSemAdministrador() {
        autenticar("ROLE_CONSULTA", "ROLE_SECRETARIA", "ROLE_FINANCEIRO");
        assertEquals(DashboardWebController.PerfilDashboard.FINANCEIRO, controller.perfilDashboard());
    }

    @Test
    void secretariaTemPrecedenciaSobreCatequeseEConsulta() {
        autenticar("ROLE_CONSULTA", "ROLE_CATEQUESE", "ROLE_SECRETARIA");
        assertEquals(DashboardWebController.PerfilDashboard.SECRETARIA, controller.perfilDashboard());
    }

    @Test
    void catequeseTemPrecedenciaSobreConsulta() {
        autenticar("ROLE_CONSULTA", "ROLE_CATEQUESE");
        assertEquals(DashboardWebController.PerfilDashboard.CATEQUESE, controller.perfilDashboard());
    }

    @Test
    void consultaEhUsadoQuandoEhUnicoPerfil() {
        autenticar("ROLE_CONSULTA");
        assertEquals(DashboardWebController.PerfilDashboard.CONSULTA, controller.perfilDashboard());
    }

    private void autenticar(String... perfis) {
        var authorities = List.of(perfis).stream().map(SimpleGrantedAuthority::new).toList();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("usuario", "senha", authorities));
    }
}
