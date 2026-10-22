package br.com.admeclesial.application.pessoa;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.pessoa.Familia;
import br.com.admeclesial.domain.pessoa.MembroFamilia;
import br.com.admeclesial.domain.pessoa.Pessoa;
import br.com.admeclesial.domain.pessoa.TipoMembroFamilia;
import br.com.admeclesial.infrastructure.persistence.pessoa.FamiliaRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.MembroFamiliaRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MembroFamiliaApplicationServiceTest {

    @Mock MembroFamiliaRepository repositorio;
    @Mock FamiliaRepository familias;
    @Mock PessoaRepository pessoas;
    @Mock SegurancaOrganizacaoService seguranca;

    private MembroFamiliaApplicationService service;

    private final UUID familiaUuid = UUID.randomUUID();
    private final UUID pessoaUuid = UUID.randomUUID();
    private Familia familia;
    private Pessoa pessoa;

    @BeforeEach
    void setUp() {
        service = new MembroFamiliaApplicationService(repositorio, familias, pessoas, seguranca);
        familia = mock(Familia.class);
        when(familia.isAtivo()).thenReturn(true);
        when(familia.getId()).thenReturn(1L);
        pessoa = mock(Pessoa.class);
        when(pessoa.isAtivo()).thenReturn(true);
        when(pessoa.getId()).thenReturn(2L);
        when(pessoa.getUuid()).thenReturn(pessoaUuid);
        when(seguranca.organizacoesAutorizadas()).thenReturn(Set.of(UUID.randomUUID()));
        when(pessoas.isVisivel(any(), anySet())).thenReturn(true);
        when(familias.findByUuid(familiaUuid)).thenReturn(Optional.of(familia));
        when(pessoas.findByUuid(pessoaUuid)).thenReturn(Optional.of(pessoa));
        when(pessoas.findById(2L)).thenReturn(Optional.of(pessoa));
    }

    @Test
    void deveCriarMembro() {
        when(repositorio.existsByFamiliaIdAndPessoaIdAndDataFimIsNull(1L, 2L)).thenReturn(false);
        when(repositorio.save(any(MembroFamilia.class))).thenAnswer(i -> i.getArgument(0));
        var m = service.criar(new MembroFamiliaRequest(familiaUuid, pessoaUuid, TipoMembroFamilia.RESPONSAVEL, LocalDate.now(), null));
        assertEquals(TipoMembroFamilia.RESPONSAVEL, m.getTipo());
        assertEquals(1L, m.getFamiliaId());
        assertEquals(2L, m.getPessoaId());
    }

    @Test
    void deveRejeitarAssociacaoDuplicada() {
        when(repositorio.existsByFamiliaIdAndPessoaIdAndDataFimIsNull(1L, 2L)).thenReturn(true);
        var ex = assertThrows(IllegalArgumentException.class, () -> service.criar(
                new MembroFamiliaRequest(familiaUuid, pessoaUuid, TipoMembroFamilia.FILHO, LocalDate.now(), null)));
        assertTrue(ex.getMessage().toLowerCase().contains("já possui"));
    }

    @Test
    void deveRejeitarFamiliaOuPessoaInativa() {
        when(pessoa.isAtivo()).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.criar(
                new MembroFamiliaRequest(familiaUuid, pessoaUuid, TipoMembroFamilia.FILHO, LocalDate.now(), null)));
    }

    @Test
    void deveEncerrarAssociacao() {
        var membroUuid = UUID.randomUUID();
        var membro = mock(MembroFamilia.class);
        when(membro.getPessoaId()).thenReturn(2L);
        when(membro.getDataInicio()).thenReturn(LocalDate.of(2024, 1, 1));
        when(repositorio.findByUuid(membroUuid)).thenReturn(Optional.of(membro));
        when(repositorio.save(membro)).thenReturn(membro);

        service.encerrar(membroUuid, LocalDate.of(2024, 6, 1));

        verify(membro).setDataFim(LocalDate.of(2024, 6, 1));
    }

    @Test
    void deveRejeitarEncerramentoComDataAnterior() {
        var membroUuid = UUID.randomUUID();
        var membro = mock(MembroFamilia.class);
        when(membro.getPessoaId()).thenReturn(2L);
        when(membro.getDataInicio()).thenReturn(LocalDate.of(2024, 6, 1));
        when(repositorio.findByUuid(membroUuid)).thenReturn(Optional.of(membro));

        assertThrows(IllegalArgumentException.class, () -> service.encerrar(membroUuid, LocalDate.of(2024, 1, 1)));
    }
}
