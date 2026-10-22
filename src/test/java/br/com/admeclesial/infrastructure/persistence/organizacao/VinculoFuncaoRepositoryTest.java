package br.com.admeclesial.infrastructure.persistence.organizacao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class VinculoFuncaoRepositoryTest {

    @Autowired
    private VinculoFuncaoRepository repository;

    @Test
    void findPessoasElegiveisComDatasNulasNaoFalha() {
        assertDoesNotThrow(() -> repository.findPessoasElegiveisComoCatequista(-1L, null, null));
    }

    @Test
    void findPessoasElegiveisComDataInicioNulaNaoFalha() {
        assertDoesNotThrow(() -> repository.findPessoasElegiveisComoCatequista(-1L, null, LocalDate.now()));
    }

    @Test
    void findPessoasElegiveisComDatasPreenchidasNaoFalha() {
        assertDoesNotThrow(() -> repository.findPessoasElegiveisComoCatequista(-1L, LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(1)));
    }
}
