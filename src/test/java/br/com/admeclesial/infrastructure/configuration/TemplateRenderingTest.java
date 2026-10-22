package br.com.admeclesial.infrastructure.configuration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TemplateRenderingTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session;

    @BeforeEach
    void autenticarAdmin() {
        var authorities = List.of(
                "ROLE_ADMINISTRADOR",
                "ROLE_SECRETARIA",
                "ROLE_CATEQUESE",
                "ROLE_FINANCEIRO",
                "ROLE_CONSULTA"
        ).stream().map(SimpleGrantedAuthority::new).toList();

        var auth = new UsernamePasswordAuthenticationToken("admin", "senha", authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveRenderizarListagensComEmptyStateSemErros() throws Exception {
        mockMvc.perform(get("/processos-matrimoniais").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/sacramentos").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/projetos").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/pessoas").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/familias").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/familias/nova").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/relacionamentos").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/organizacoes-eclesiais").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/usuarios").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/funcoes-eclesiais").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/vinculos-pessoa-organizacao").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/vinculos-funcoes-eclesiais").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/catequeses").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro").session(session)).andExpect(status().isOk());
    }
    @Test
    void menuAbreApenasOGrupoAtivo() throws Exception {
        assertGrupoAberto("/financeiro/contas", "Financeiro");
        assertGrupoAberto("/pessoas", "Cadastros");
        assertGrupoAberto("/familias", "Cadastros");
        assertGrupoAberto("/funcoes-eclesiais", "Vínculos e Funções");
        assertGrupoAberto("/catequeses", "Pastoral");
        assertGrupoAberto("/projetos", "Projetos");
        assertGrupoAberto("/relatorios/pessoas", "Relatórios");
        assertNenhumGrupoAberto("/dashboard");
    }

    private void assertGrupoAberto(String url, String titulo) throws Exception {
        String body = mockMvc.perform(get(url).session(session)).andReturn().getResponse().getContentAsString();
        var m = java.util.regex.Pattern.compile("<details[^>]*>\\s*<summary class=\"nav-group-title\">" + java.util.regex.Pattern.quote(titulo) + "</summary>", java.util.regex.Pattern.DOTALL).matcher(body);
        org.junit.jupiter.api.Assertions.assertTrue(m.find(), "grupo '" + titulo + "' deve existir em " + url);
        org.junit.jupiter.api.Assertions.assertTrue(m.group().contains("open"), "grupo '" + titulo + "' deveria estar aberto em " + url);
        org.junit.jupiter.api.Assertions.assertEquals(1, contar(body, "open=\"open\""), "apenas um grupo aberto em " + url);
    }

    private void assertNenhumGrupoAberto(String url) throws Exception {
        String body = mockMvc.perform(get(url).session(session)).andReturn().getResponse().getContentAsString();
        org.junit.jupiter.api.Assertions.assertEquals(0, contar(body, "open=\"open\""), "nenhum grupo aberto em " + url);
    }

    private static int contar(String s, String sub) {
        int c = 0, i = 0;
        while ((i = s.indexOf(sub, i)) >= 0) { c++; i += sub.length(); }
        return c;
    }

    @Test
    void deveRenderizarTelasDeCadastroSemErros() throws Exception {
        mockMvc.perform(get("/familias/nova").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/catequeses/nova").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/pessoas/nova").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/organizacoes-eclesiais/nova").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/sacramentos/novo").session(session)).andExpect(status().isOk());
    }

    @Test
    void deveRenderizarTelasFinanceirasSemErros() throws Exception {
        mockMvc.perform(get("/financeiro/contas").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/contas/nova").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/categorias").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/categorias/nova").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/centros-custo").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/centros-custo/novo").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/fornecedores").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/fornecedores/novo").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/dizimistas").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/dizimistas/novo").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/movimentacoes").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/movimentacoes/nova").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/transferencias").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/transferencias/nova").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/contas-a-pagar").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/contas-a-pagar/nova").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/pagamentos").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/pagamentos/novo").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/regras-repasse").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/regras-repasse/nova").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/obrigacoes-repasse").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/obrigacoes-repasse/nova").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/orcamentos").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/financeiro/orcamentos/novo").session(session)).andExpect(status().isOk());
    }
}
