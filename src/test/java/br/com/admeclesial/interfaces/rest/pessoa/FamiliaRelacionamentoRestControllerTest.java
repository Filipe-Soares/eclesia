package br.com.admeclesial.interfaces.rest.pessoa;

import br.com.admeclesial.application.pessoa.*;
import br.com.admeclesial.domain.pessoa.Familia;
import br.com.admeclesial.domain.pessoa.MembroFamilia;
import br.com.admeclesial.domain.pessoa.RelacionamentoPessoa;
import br.com.admeclesial.domain.pessoa.TipoRelacionamentoPessoa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamiliaRelacionamentoRestControllerTest {

    @Mock FamiliaApplicationService familias;
    @Mock MembroFamiliaApplicationService membros;
    @Mock RelacionamentoPessoaApplicationService relacionamentos;

    @InjectMocks FamiliaRestController familiaController;
    @InjectMocks MembroFamiliaRestController membroController;
    @InjectMocks RelacionamentoPessoaRestController relacionamentoController;

    @Test
    void familiaBuscarDelegaAoService() {
        var uuid = UUID.randomUUID();
        var f = new Familia();
        when(familias.buscar(uuid)).thenReturn(f);
        assertSame(f, familiaController.buscar(uuid));
    }

    @Test
    void familiaListarDelegaAoService() {
        when(familias.listar()).thenReturn(List.of(new Familia()));
        assertEquals(1, familiaController.listar().size());
    }

    @Test
    void familiaAtualizarDelegaAoService() {
        var uuid = UUID.randomUUID();
        var f = new Familia();
        when(familias.atualizar(eq(uuid), any())).thenReturn(f);
        assertSame(f, familiaController.atualizar(uuid, new FamiliaRequest("N", true)));
    }

    @Test
    void membroEncerrarDelegaAoService() {
        var uuid = UUID.randomUUID();
        var data = LocalDate.now();
        var m = new MembroFamilia();
        when(membros.encerrar(uuid, data)).thenReturn(m);
        assertSame(m, membroController.encerrar(uuid, data));
    }

    @Test
    void membroPorFamiliaDelegaAoService() {
        var uuid = UUID.randomUUID();
        when(membros.listarPorFamilia(uuid)).thenReturn(List.of());
        assertEquals(0, membroController.porFamilia(uuid).size());
    }

    @Test
    void relacionamentoAtualizarDelegaAoService() {
        var uuid = UUID.randomUUID();
        var r = new RelacionamentoPessoa();
        when(relacionamentos.atualizar(eq(uuid), any())).thenReturn(r);
        assertSame(r, relacionamentoController.atualizar(uuid,
                new RelacionamentoPessoaRequest(UUID.randomUUID(), UUID.randomUUID(), TipoRelacionamentoPessoa.CONJUGE, LocalDate.now(), null, null)));
    }

    @Test
    void relacionamentoRemoverDelegaAoService() {
        var uuid = UUID.randomUUID();
        relacionamentoController.remover(uuid);
        verify(relacionamentos).remover(uuid);
    }

    @Test
    void relacionamentoPorPessoaDelegaAoService() {
        var uuid = UUID.randomUUID();
        when(relacionamentos.porPessoa(uuid)).thenReturn(List.of());
        assertEquals(0, relacionamentoController.porPessoa(uuid).size());
    }
}
