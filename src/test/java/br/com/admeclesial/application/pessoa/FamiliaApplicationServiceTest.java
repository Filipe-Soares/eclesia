package br.com.admeclesial.application.pessoa;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.pessoa.Familia;
import br.com.admeclesial.domain.pessoa.MembroFamilia;
import br.com.admeclesial.domain.pessoa.Pessoa;
import br.com.admeclesial.infrastructure.persistence.pessoa.FamiliaRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.MembroFamiliaRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamiliaApplicationServiceTest {

    @Mock FamiliaRepository repositorio;
    @Mock MembroFamiliaRepository membros;
    @Mock PessoaRepository pessoas;
    @Mock SegurancaOrganizacaoService seguranca;

    private FamiliaApplicationService service;

    @BeforeEach
    void setUp() {
        service = new FamiliaApplicationService(repositorio, membros, pessoas, seguranca);
    }

    @Test
    void deveCriarFamiliaAtivaPorPadrao() {
        when(repositorio.save(any(Familia.class))).thenAnswer(i -> i.getArgument(0));
        var f = service.criar(new FamiliaRequest("Família Silva", null));
        assertEquals("Família Silva", f.getNome());
        assertTrue(f.isAtivo());
    }

    @Test
    void deveRejeitarCriacaoSemNome() {
        assertThrows(IllegalArgumentException.class, () -> service.criar(new FamiliaRequest("  ", true)));
    }

    @Test
    void administradorVeTodasAsFamilias() {
        when(seguranca.ehAdministrador()).thenReturn(true);
        when(repositorio.findAll()).thenReturn(List.of(new Familia(), new Familia()));
        assertEquals(2, service.listar().size());
    }

    @Test
    void naoAdministradorNaoVeFamiliaVazia() {
        var f = mock(Familia.class);
        when(f.getId()).thenReturn(10L);
        when(seguranca.ehAdministrador()).thenReturn(false);
        when(seguranca.organizacoesAutorizadas()).thenReturn(Set.of(UUID.randomUUID()));
        when(repositorio.findAll()).thenReturn(List.of(f));
        when(membros.findByFamiliaId(any())).thenReturn(List.of());
        assertTrue(service.listar().isEmpty());
    }

    @Test
    void naoAdministradorVeFamiliaComMembroVisivel() {
        var f = mock(Familia.class);
        when(f.getId()).thenReturn(10L);
        var m = mock(MembroFamilia.class);
        when(m.getPessoaId()).thenReturn(99L);
        var p = mock(Pessoa.class);
        when(p.getUuid()).thenReturn(UUID.randomUUID());

        when(seguranca.ehAdministrador()).thenReturn(false);
        when(seguranca.organizacoesAutorizadas()).thenReturn(Set.of(UUID.randomUUID()));
        when(repositorio.findAll()).thenReturn(List.of(f));
        when(membros.findByFamiliaId(10L)).thenReturn(List.of(m));
        when(pessoas.findById(99L)).thenReturn(Optional.of(p));
        when(pessoas.isVisivel(any(), anySet())).thenReturn(true);

        assertEquals(1, service.listar().size());
    }

    @Test
    void deveNegarBuscaDeFamiliaNaoVisivel() {
        var uuid = UUID.randomUUID();
        var f = mock(Familia.class);
        when(f.getId()).thenReturn(10L);
        when(repositorio.findByUuid(uuid)).thenReturn(Optional.of(f));
        when(seguranca.ehAdministrador()).thenReturn(false);
        when(seguranca.organizacoesAutorizadas()).thenReturn(Set.of(UUID.randomUUID()));
        when(membros.findByFamiliaId(anyLong())).thenReturn(List.of());
        assertThrows(AccessDeniedException.class, () -> service.buscar(uuid));
    }

    @Test
    void deveInativarFamilia() {
        var uuid = UUID.randomUUID();
        var f = mock(Familia.class);
        when(repositorio.findByUuid(uuid)).thenReturn(Optional.of(f));
        when(seguranca.ehAdministrador()).thenReturn(true);
        when(repositorio.save(f)).thenReturn(f);
        service.inativar(uuid);
        verify(f).setAtivo(false);
    }
}
