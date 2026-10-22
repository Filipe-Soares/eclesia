package br.com.admeclesial.application.financeiro;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.financeiro.ContaFinanceira;
import br.com.admeclesial.domain.financeiro.TipoContaFinanceira;
import br.com.admeclesial.infrastructure.persistence.financeiro.ContaFinanceiraRepository;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContaFinanceiraIsolamentoOrganizacaoTest {
    @Test
    void deveNegarCriacaoNaOrganizacaoB() {
        var repo = mock(ContaFinanceiraRepository.class);
        var orgs = mock(OrganizacaoEclesialRepository.class);
        var seguranca = mock(SegurancaOrganizacaoService.class);
        var service = new ContaFinanceiraApplicationService(repo, orgs);
        service.seguranca = seguranca;
        var orgB = UUID.randomUUID();
        doThrow(new AccessDeniedException("A->B")).when(seguranca).validarAcesso(orgB);

        assertThrows(AccessDeniedException.class, () -> service.salvar(null, orgB, "Caixa", TipoContaFinanceira.CAIXA, null, null, null, true));
        verifyNoInteractions(repo, orgs);
    }

    @Test
    void listagemNaoDeveExporContaDaOrganizacaoB() {
        var repo = mock(ContaFinanceiraRepository.class);
        var orgs = mock(OrganizacaoEclesialRepository.class);
        var seguranca = mock(SegurancaOrganizacaoService.class);
        var service = new ContaFinanceiraApplicationService(repo, orgs);
        service.seguranca = seguranca;
        var a = mock(ContaFinanceira.class); var b = mock(ContaFinanceira.class);
        when(a.getOrganizacaoEclesialId()).thenReturn(10L); when(b.getOrganizacaoEclesialId()).thenReturn(20L);
        when(repo.findAllByOrderByNomeAsc()).thenReturn(List.of(a,b));
        doThrow(new AccessDeniedException("A->B")).when(seguranca).validarAcessoId(20L);

        assertEquals(List.of(a), service.listar());
    }
}
