package br.com.admeclesial.application.matrimonio;

import br.com.admeclesial.domain.matrimonio.*;
import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.domain.pessoa.Pessoa;
import br.com.admeclesial.domain.sacramento.*;
import br.com.admeclesial.infrastructure.persistence.matrimonio.*;
import br.com.admeclesial.infrastructure.persistence.sacramento.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class MatrimonioApplicationServiceTest {
    @Mock MatrimonioRepository repo; @Mock ProcessoMatrimonialRepository proc; @Mock CelebracaoMatrimonialRepository cele;
    @Mock SacramentoRepository sacramentos; @Mock ParticipanteSacramentoRepository participantes;

    @Test void deveCriarSacramentoEParticipantesAoRegistrarMatrimonio(){
        OrganizacaoEclesial o=new OrganizacaoEclesial(); o.setAtivo(true); Pessoa p1=new Pessoa();p1.setAtivo(true);Pessoa p2=new Pessoa();p2.setAtivo(true);
        ProcessoMatrimonial p=new ProcessoMatrimonial(); try{var f=ProcessoMatrimonial.class.getDeclaredField("id");f.setAccessible(true);f.set(p,1L);}catch(Exception e){throw new RuntimeException(e);} p.setOrganizacao(o);p.setNubente1(p1);p.setNubente2(p2);p.setDataAbertura(LocalDate.of(2026,1,1));p.setStatus(StatusProcessoMatrimonial.EM_ANALISE);
        CelebracaoMatrimonial c=new CelebracaoMatrimonial();c.setProcesso(p);c.setOrganizacao(o);c.setDataCelebracao(LocalDate.of(2026,6,1));
        UUID pu=UUID.randomUUID(), cu=UUID.randomUUID(); when(proc.findByUuid(pu)).thenReturn(Optional.of(p)); when(cele.findByUuid(cu)).thenReturn(Optional.of(c));
        when(repo.findByProcessoId(anyLong())).thenReturn(Optional.empty()); when(sacramentos.save(any())).thenAnswer(i->i.getArgument(0)); when(repo.save(any())).thenAnswer(i->i.getArgument(0));
        MatrimonioApplicationService s=new MatrimonioApplicationService(repo,proc,cele,sacramentos,participantes);
        s.salvar(new MatrimonioRequest(pu,cu,LocalDate.of(2026,6,2),"obs"));
        ArgumentCaptor<Sacramento> cap=ArgumentCaptor.forClass(Sacramento.class); verify(sacramentos).save(cap.capture());
        assertEquals(TipoSacramento.MATRIMONIO,cap.getValue().getTipo()); assertEquals(o,cap.getValue().getOrganizacaoEclesial()); assertEquals(c.getDataCelebracao(),cap.getValue().getDataCelebracao());
        verify(participantes,times(2)).save(any(ParticipanteSacramento.class)); assertEquals(StatusProcessoMatrimonial.CONCLUIDO,p.getStatus()); assertEquals(LocalDate.of(2026,6,2),p.getDataEncerramento());
    }

    @Test void naoPermiteCelebracaoDeOutraOrganizacao(){
        OrganizacaoEclesial o1=new OrganizacaoEclesial(); OrganizacaoEclesial o2=new OrganizacaoEclesial(); Pessoa p1=new Pessoa(); Pessoa p2=new Pessoa(); try{var f=OrganizacaoEclesial.class.getDeclaredField("id");f.setAccessible(true);f.set(o1,1L);f.set(o2,2L);}catch(Exception e){throw new RuntimeException(e);}
        ProcessoMatrimonial p=new ProcessoMatrimonial();p.setOrganizacao(o1);p.setNubente1(p1);p.setNubente2(p2);p.setDataAbertura(LocalDate.of(2026,1,1));p.setStatus(StatusProcessoMatrimonial.EM_ANALISE);
        CelebracaoMatrimonial c=new CelebracaoMatrimonial();c.setProcesso(p);c.setOrganizacao(o2);c.setDataCelebracao(LocalDate.of(2026,6,1));
        UUID pu=UUID.randomUUID(),cu=UUID.randomUUID();when(proc.findByUuid(pu)).thenReturn(Optional.of(p));when(cele.findByUuid(cu)).thenReturn(Optional.of(c));
        MatrimonioApplicationService s=new MatrimonioApplicationService(repo,proc,cele,sacramentos,participantes);
        assertThrows(IllegalArgumentException.class,()->s.salvar(new MatrimonioRequest(pu,cu,LocalDate.of(2026,6,2),null))); verifyNoInteractions(sacramentos,participantes);
    }
}
