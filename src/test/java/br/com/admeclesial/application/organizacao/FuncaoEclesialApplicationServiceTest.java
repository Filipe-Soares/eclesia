package br.com.admeclesial.application.organizacao;

import br.com.admeclesial.domain.organizacao.FuncaoEclesial;
import br.com.admeclesial.infrastructure.persistence.organizacao.FuncaoEclesialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncaoEclesialApplicationServiceTest {

    @Mock private FuncaoEclesialRepository repository;
    @InjectMocks private FuncaoEclesialApplicationService service;

    @Test
    void deveNormalizarCodigoERejeitarCodigoDuplicado() {
        when(repository.existsByCodigo("CATEQUISTA")).thenReturn(true);

        var request = new FuncaoEclesialRequest(" catequista ", "Catequista", null, false);

        var ex = assertThrows(VinculoBusinessException.class, () -> service.criar(request));

        assertEquals("Já existe uma função eclesial com o código informado.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveSalvarFuncaoComCodigoNormalizado() {
        when(repository.existsByCodigo("CATEQUISTA")).thenReturn(false);
        when(repository.save(any(FuncaoEclesial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.criar(new FuncaoEclesialRequest(" catequista ", "Catequista", "  Descrição  ", false));

        assertEquals("CATEQUISTA", response.codigo());
        assertEquals("Catequista", response.nome());
        assertEquals("Descrição", response.descricao());
        verify(repository).save(any(FuncaoEclesial.class));
    }
}
