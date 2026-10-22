package br.com.admeclesial.infrastructure.persistence.relatorio;

import br.com.admeclesial.infrastructure.persistence.projeto.ProjetoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class RelatoriosFiltrosDatasNulasTest {

    @Autowired ProjetoRepository projetos;
    @Autowired RelatorioContaPagarRepository contas;
    @Autowired RelatorioContribuicaoRepository contribuicoes;
    @Autowired RelatorioMovimentacaoRepository movimentacoes;
    @Autowired RelatorioRepasseRepository repasses;
    @Autowired RelatorioSacramentoRepository sacramentos;

    private final UUID org = UUID.randomUUID();
    private final PageRequest pg = PageRequest.of(0, 10);

    @Test
    void projetoComFiltrosNulosNaoFalha() {
        assertDoesNotThrow(() -> projetos.pesquisarRelatorio(-1L, null, null, null, null, false, pg));
    }

    @Test
    void contaPagarComFiltrosNulosNaoFalha() {
        assertDoesNotThrow(() -> {
            contas.pesquisar(org, null, null, null, null, false, pg);
            contas.resumo(org, null, null, null, null, false);
        });
    }

    @Test
    void contribuicaoComFiltrosNulosNaoFalha() {
        assertDoesNotThrow(() -> {
            contribuicoes.registros(org, null, null, null, null, null, pg);
            contribuicoes.resumo(org, null, null, null, null, null);
            contribuicoes.porDizimista(org, null, null, null, null, pg);
        });
    }

    @Test
    void movimentacaoComFiltrosNulosNaoFalha() {
        assertDoesNotThrow(() -> {
            movimentacoes.pesquisar(org, null, null, null, null, null, null, pg);
            movimentacoes.resumo(org, null, null, null, null, null, null);
        });
    }

    @Test
    void repasseComFiltrosNulosNaoFalha() {
        assertDoesNotThrow(() -> {
            repasses.pesquisar(org, null, null, null, false, pg);
            repasses.resumo(org, null, null, null, false);
        });
    }

    @Test
    void sacramentoComFiltrosNulosNaoFalha() {
        assertDoesNotThrow(() -> {
            sacramentos.registros(org, null, null, null, null, pg);
            sacramentos.pessoas(org, null, null, null, null, pg);
        });
    }
}
