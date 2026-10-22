package br.com.admeclesial.interfaces.web.familia;

import org.junit.jupiter.api.AfterEach;
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

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class FamiliaFormErrorsAndAutorizacaoTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession sessao(String... roles) {
        var authorities = Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList();
        var auth = new UsernamePasswordAuthenticationToken("admin", "senha", authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        var session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        return session;
    }

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void postInvalidoExibeMensagemDeErroDeBinding() throws Exception {
        mockMvc.perform(post("/familias")
                        .session(sessao("ROLE_ADMINISTRADOR"))
                        .with(csrf())
                        .param("nome", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("familia/form"))
                .andExpect(content().string(containsString("Corrija os itens")));
    }

    @Test
    void postNegadoParaPerfilSemPermissaoDeEscrita() throws Exception {
        mockMvc.perform(post("/familias")
                        .session(sessao("ROLE_CONSULTA"))
                        .with(csrf())
                        .param("nome", "Família Teste"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getNegadoParaUsuarioSemPerfil() throws Exception {
        mockMvc.perform(get("/familias").session(sessao()))
                .andExpect(status().isForbidden());
    }
}
