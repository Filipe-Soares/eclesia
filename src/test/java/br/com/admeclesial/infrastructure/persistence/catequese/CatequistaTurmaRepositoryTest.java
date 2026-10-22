package br.com.admeclesial.infrastructure.persistence.catequese;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class CatequistaTurmaRepositoryTest {

    @Autowired
    private CatequistaTurmaRepository repository;

    @Test
    void existsPessoaConflitanteComFimEIgnorarNulosNaoFalha() {
        assertDoesNotThrow(() -> repository.existsPessoaConflitante(-1L, -1L, LocalDate.now(), null, null));
    }

    @Test
    void existsPrincipalConflitanteComFimEIgnorarNulosNaoFalha() {
        assertDoesNotThrow(() -> repository.existsPrincipalConflitante(-1L, LocalDate.now(), null, null));
    }

    @Test
    void existsPessoaConflitanteComDatasPreenchidasNaoFalha() {
        assertDoesNotThrow(() -> repository.existsPessoaConflitante(-1L, -1L, LocalDate.now(), LocalDate.now().plusMonths(1), 5L));
    }
}
