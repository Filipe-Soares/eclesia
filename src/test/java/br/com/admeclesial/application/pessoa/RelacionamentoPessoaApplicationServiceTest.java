package br.com.admeclesial.application.pessoa;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.pessoa.Pessoa;
import br.com.admeclesial.domain.pessoa.RelacionamentoPessoa;
import br.com.admeclesial.domain.pessoa.TipoRelacionamentoPessoa;
import br.com.admeclesial.domain.pessoa.TipoSexo;
import br.com.admeclesial.infrastructure.persistence.pessoa.PessoaRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.RelacionamentoPessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RelacionamentoPessoaApplicationServiceTest {

    @Mock RelacionamentoPessoaRepository repositorio;
    @Mock PessoaRepository pessoas;
    @Mock SegurancaOrganizacaoService seguranca;

    private RelacionamentoPessoaApplicationService service;

    private final UUID uuidA = UUID.randomUUID();
    private final UUID uuidB = UUID.randomUUID();
    private Pessoa a;
    private Pessoa b;

    @BeforeEach
    void setUp() {
        service = new RelacionamentoPessoaApplicationService(repositorio, pessoas, seguranca);
        a = pessoa(1L, uuidA, TipoSexo.MASCULINO);
        b = pessoa(2L, uuidB, TipoSexo.MASCULINO);

        when(seguranca.organizacoesAutorizadas()).thenReturn(Set.of(UUID.randomUUID()));
        when(pessoas.isVisivel(any(), anySet())).thenReturn(true);
        when(pessoas.findByUuid(uuidA)).thenReturn(Optional.of(a));
        when(pessoas.findByUuid(uuidB)).thenReturn(Optional.of(b));
        when(repositorio.save(any(RelacionamentoPessoa.class))).thenAnswer(i -> i.getArgument(0));
        when(repositorio.findByPessoaOrigemIdAndPessoaDestinoId(2L, 1L)).thenReturn(List.of());
    }

    private Pessoa pessoa(Long id, UUID uuid, TipoSexo sexo) {
        Pessoa p = mock(Pessoa.class);
        when(p.getId()).thenReturn(id);
        when(p.getUuid()).thenReturn(uuid);
        when(p.isAtivo()).thenReturn(true);
        when(p.getSexo()).thenReturn(sexo);
        return p;
    }

    @Test
    void deveCriarRelacionamentoECriarInversoSimples() {
        service.criar(new RelacionamentoPessoaRequest(uuidA, uuidB, TipoRelacionamentoPessoa.PAI, LocalDate.now(), null, null));

        var captor = ArgumentCaptor.forClass(RelacionamentoPessoa.class);
        verify(repositorio, times(2)).save(captor.capture());
        var inverso = captor.getAllValues().stream().filter(r -> Long.valueOf(2L).equals(r.getPessoaOrigemId())).findFirst().orElseThrow();
        assertEquals(TipoRelacionamentoPessoa.FILHO, inverso.getTipoRelacionamento());
        assertEquals(1L, inverso.getPessoaDestinoId());
    }

    @Test
    void deveCriarInversoDependenteDeSexo() {
        when(b.getSexo()).thenReturn(TipoSexo.FEMININO);
        service.criar(new RelacionamentoPessoaRequest(uuidA, uuidB, TipoRelacionamentoPessoa.FILHO, LocalDate.now(), null, null));

        var captor = ArgumentCaptor.forClass(RelacionamentoPessoa.class);
        verify(repositorio, times(2)).save(captor.capture());
        var inverso = captor.getAllValues().stream().filter(r -> Long.valueOf(2L).equals(r.getPessoaOrigemId())).findFirst().orElseThrow();
        assertEquals(TipoRelacionamentoPessoa.MAE, inverso.getTipoRelacionamento());
    }

    @Test
    void deveCriarInversoSimetrico() {
        service.criar(new RelacionamentoPessoaRequest(uuidA, uuidB, TipoRelacionamentoPessoa.CONJUGE, LocalDate.now(), null, null));

        var captor = ArgumentCaptor.forClass(RelacionamentoPessoa.class);
        verify(repositorio, times(2)).save(captor.capture());
        var inverso = captor.getAllValues().stream().filter(r -> Long.valueOf(2L).equals(r.getPessoaOrigemId())).findFirst().orElseThrow();
        assertEquals(TipoRelacionamentoPessoa.CONJUGE, inverso.getTipoRelacionamento());
    }

    @Test
    void deveRemoverParInverso() {
        var uuid = UUID.randomUUID();
        var rel = mock(RelacionamentoPessoa.class);
        when(rel.getPessoaOrigemId()).thenReturn(1L);
        when(rel.getPessoaDestinoId()).thenReturn(2L);
        when(rel.getTipoRelacionamento()).thenReturn(TipoRelacionamentoPessoa.CONJUGE);
        when(repositorio.findByUuid(uuid)).thenReturn(Optional.of(rel));
        when(pessoas.findById(1L)).thenReturn(Optional.of(a));
        when(pessoas.findById(2L)).thenReturn(Optional.of(b));

        var inverso = mock(RelacionamentoPessoa.class);
        when(inverso.getTipoRelacionamento()).thenReturn(TipoRelacionamentoPessoa.CONJUGE);
        when(repositorio.findByPessoaOrigemIdAndPessoaDestinoId(2L, 1L)).thenReturn(List.of(inverso));

        service.remover(uuid);

        verify(repositorio).delete(inverso);
        verify(repositorio).delete(rel);
    }

    @Test
    void deveRejeitarPessoasIguais() {
        when(pessoas.findByUuid(uuidA)).thenReturn(Optional.of(a));
        assertThrows(IllegalArgumentException.class, () -> service.criar(
                new RelacionamentoPessoaRequest(uuidA, uuidA, TipoRelacionamentoPessoa.CONJUGE, LocalDate.now(), null, null)));
    }

    @Test
    void deveRejeitarDataInicioAusente() {
        assertThrows(IllegalArgumentException.class, () -> service.criar(
                new RelacionamentoPessoaRequest(uuidA, uuidB, TipoRelacionamentoPessoa.PAI, null, null, null)));
    }
}
