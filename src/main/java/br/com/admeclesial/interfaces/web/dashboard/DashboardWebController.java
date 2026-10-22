package br.com.admeclesial.interfaces.web.dashboard;

import br.com.admeclesial.application.dashboard.DashboardApplicationService;
import br.com.admeclesial.application.relatorio.RelatorioContextoService;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@Controller
public class DashboardWebController {
    private final DashboardApplicationService service;
    private final RelatorioContextoService contexto;
    private final SegurancaOrganizacaoService segurancaOrganizacao;

    public DashboardWebController(DashboardApplicationService service,
                                  RelatorioContextoService contexto,
                                  SegurancaOrganizacaoService segurancaOrganizacao) {
        this.service = service;
        this.contexto = contexto;
        this.segurancaOrganizacao = segurancaOrganizacao;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) UUID organizacaoUuid,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
                            Model model) {
        var orgs = contexto.organizacoesAutorizadas();
        model.addAttribute("organizacoes", orgs);

        if (organizacaoUuid == null && !orgs.isEmpty()) {
            organizacaoUuid = orgs.get(0).uuid();
        }
        if (organizacaoUuid != null) {
            segurancaOrganizacao.validarAcesso(organizacaoUuid);
        }

        var hoje = LocalDate.now();
        if (inicio == null) inicio = hoje.withDayOfMonth(1);
        if (fim == null) fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Período final não pode ser anterior ao inicial");
        }

        model.addAttribute("organizacaoUuid", organizacaoUuid);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);

        var perfil = perfilDashboard();
        if (organizacaoUuid == null) {
            return template(perfil);
        }

        switch (perfil) {
            case ADMINISTRADOR -> model.addAttribute("dashboard", service.administrador(organizacaoUuid, inicio, fim));
            case FINANCEIRO -> model.addAttribute("dashboard", service.financeiro(organizacaoUuid, inicio, fim));
            case SECRETARIA -> model.addAttribute("dashboard", service.secretaria(organizacaoUuid, inicio, fim));
            case CATEQUESE -> model.addAttribute("dashboard", service.catequese(organizacaoUuid, inicio, fim));
            case CONSULTA -> model.addAttribute("dashboard", service.consulta(organizacaoUuid, inicio, fim));
        }
        return template(perfil);
    }

    PerfilDashboard perfilDashboard() {
        if (temPerfil("ROLE_ADMINISTRADOR")) return PerfilDashboard.ADMINISTRADOR;
        if (temPerfil("ROLE_FINANCEIRO")) return PerfilDashboard.FINANCEIRO;
        if (temPerfil("ROLE_SECRETARIA")) return PerfilDashboard.SECRETARIA;
        if (temPerfil("ROLE_CATEQUESE")) return PerfilDashboard.CATEQUESE;
        if (temPerfil("ROLE_CONSULTA")) return PerfilDashboard.CONSULTA;
        throw new AccessDeniedException("Usuário sem perfil de acesso ao dashboard");
    }

    private String template(PerfilDashboard perfil) {
        return "dashboard/" + perfil.name().toLowerCase();
    }

    private boolean temPerfil(String perfil) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(perfil));
    }

    enum PerfilDashboard {
        ADMINISTRADOR, FINANCEIRO, SECRETARIA, CATEQUESE, CONSULTA
    }
}
