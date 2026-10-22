package br.com.admeclesial.infrastructure.persistence.organizacao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FuncaoEclesialSeedTest {

    @Autowired
    private FuncaoEclesialRepository repository;

    @Test
    void seedDeveCriarFuncaoCatequista() {
        assertTrue(repository.existsByCodigo("CATEQUISTA"),
                "A migration V10 deve criar a função eclesial CATEQUISTA por padrão.");
    }
}
