package br.com.admeclesial.application.pessoa;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.pessoa.Pessoa;
import br.com.admeclesial.domain.pessoa.TipoSexo;
import br.com.admeclesial.infrastructure.persistence.pessoa.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PessoaApplicationServiceTest {

    @Mock PessoaRepository repository;
    @Mock SegurancaOrganizacaoService seguranca;

    private PessoaApplicationService service;

    @BeforeEach
    void setUp() {
        service = new PessoaApplicationService(repository, seguranca);
    }

    @Test
    void deveBuscarPessoaComSucessoQuandoVisivel() {
        UUID uuid = UUID.randomUUID();
        UUID orgUuid = UUID.randomUUID();
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João da Silva");
        pessoa.setSexo(TipoSexo.MASCULINO);

        when(seguranca.organizacoesAutorizadas()).thenReturn(Set.of(orgUuid));
        when(repository.isVisivel(eq(uuid), anySet())).thenReturn(true);
        when(repository.findByUuid(uuid)).thenReturn(Optional.of(pessoa));

        PessoaResponse response = service.buscar(uuid);

        assertNotNull(response);
        assertEquals("João da Silva", response.nome());
    }

    @Test
    void deveNegarAcessoQuandoPessoaNaoForVisivel() {
        UUID uuid = UUID.randomUUID();
        UUID orgUuid = UUID.randomUUID();

        when(seguranca.organizacoesAutorizadas()).thenReturn(Set.of(orgUuid));
        when(repository.isVisivel(eq(uuid), anySet())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.buscar(uuid));
    }

    @Test
    void deveListarPessoasVisiveis() {
        UUID orgUuid = UUID.randomUUID();
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria Santos");
        pessoa.setSexo(TipoSexo.FEMININO);

        when(seguranca.organizacoesAutorizadas()).thenReturn(Set.of(orgUuid));
        when(repository.findVisiveis(anySet())).thenReturn(List.of(pessoa));

        List<PessoaResponse> lista = service.listar();

        assertEquals(1, lista.size());
        assertEquals("Maria Santos", lista.get(0).nome());
    }
}
