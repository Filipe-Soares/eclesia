package br.com.admeclesial.application.projeto;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.projeto.Projeto;
import br.com.admeclesial.domain.projeto.TipoProjeto;
import br.com.admeclesial.infrastructure.persistence.financeiro.ItemOrcamentoRepository;
import br.com.admeclesial.infrastructure.persistence.financeiro.OrcamentoRepository;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import br.com.admeclesial.infrastructure.persistence.projeto.FonteRecursoProjetoRepository;
import br.com.admeclesial.infrastructure.persistence.projeto.ProjetoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjetoIsolamentoOrganizacaoTest {
    private ProjetoApplicationService service(ProjetoRepository repo, SegurancaOrganizacaoService seguranca) {
        var s = new ProjetoApplicationService(repo, mock(OrganizacaoEclesialRepository.class), mock(ItemOrcamentoRepository.class), mock(OrcamentoRepository.class), mock(FonteRecursoProjetoRepository.class));
        s.seguranca = seguranca; return s;
    }

    @Test
    void deveNegarCriacaoNaOrganizacaoB() {
        var repo=mock(ProjetoRepository.class); var seguranca=mock(SegurancaOrganizacaoService.class); var s=service(repo,seguranca); var orgB=UUID.randomUUID();
        doThrow(new AccessDeniedException("A->B")).when(seguranca).validarAcesso(orgB);
        var req=new ProjetoRequest(orgB,null, TipoProjeto.PROJETO,"Projeto B",null,null,null,null);
        assertThrows(AccessDeniedException.class,()->s.criar(req));
        verifyNoInteractions(repo);
    }

    @Test
    void listagemNaoDeveExporProjetoDaOrganizacaoB() {
        var repo=mock(ProjetoRepository.class); var seguranca=mock(SegurancaOrganizacaoService.class); var s=service(repo,seguranca);
        var a=mock(Projeto.class);var b=mock(Projeto.class);when(a.getOrganizacaoEclesialId()).thenReturn(10L);when(b.getOrganizacaoEclesialId()).thenReturn(20L);when(repo.findAllByOrderByDataInicioDesc()).thenReturn(List.of(a,b));doThrow(new AccessDeniedException("A->B")).when(seguranca).validarAcessoId(20L);
        assertEquals(List.of(a),s.listar());
    }
}
