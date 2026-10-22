package br.com.admeclesial.application.sacramento;
import br.com.admeclesial.domain.organizacao.*; import br.com.admeclesial.domain.sacramento.*; import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository; import br.com.admeclesial.infrastructure.persistence.sacramento.SacramentoRepository; import org.junit.jupiter.api.*; import org.mockito.*; import java.time.LocalDate; import java.util.*; import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
class SacramentoApplicationServiceTest { @Mock SacramentoRepository repository; @Mock OrganizacaoEclesialRepository organizacaoRepository; SacramentoApplicationService service; UUID orgUuid; OrganizacaoEclesial org;
 @BeforeEach void setUp(){MockitoAnnotations.openMocks(this);service=new SacramentoApplicationService(repository,organizacaoRepository);orgUuid=UUID.randomUUID();org=new OrganizacaoEclesial();org.setNome("Paróquia"); when(organizacaoRepository.findByUuid(orgUuid)).thenReturn(Optional.of(org)); when(repository.save(any())).thenAnswer(i->i.getArgument(0));}
 @Test void deveCriar(){var r=service.criar(new SacramentoRequest(orgUuid,TipoSacramento.BATISMO,LocalDate.of(2026,1,10),"Matriz",null," obs "));assertEquals(TipoSacramento.BATISMO,r.tipo());assertEquals("obs",r.observacao());verify(repository).save(any());}
 @Test void naoDeveCriarComOrganizacaoInativa(){org.setAtivo(false);var ex=assertThrows(SacramentoBusinessException.class,()->service.criar(new SacramentoRequest(orgUuid,TipoSacramento.BATISMO,LocalDate.now(),null,null,null)));assertTrue(ex.getMessage().contains("inativa"));}
 @Test void deveBuscar(){var s=new Sacramento();s.setOrganizacaoEclesial(org);s.setTipo(TipoSacramento.CRISMA);s.setDataCelebracao(LocalDate.now());s.setStatus(StatusSacramento.REGISTRADO);UUID u=UUID.randomUUID();try{var f=s.getClass().getDeclaredField("uuid");f.setAccessible(true);f.set(s,u);}catch(Exception e){throw new RuntimeException(e);}when(repository.findByUuid(u)).thenReturn(Optional.of(s));assertEquals(u,service.buscar(u).uuid());}
 @Test void deveLancarNotFound(){var u=UUID.randomUUID();when(repository.findByUuid(u)).thenReturn(Optional.empty());assertThrows(SacramentoNotFoundException.class,()->service.buscar(u));}

    @Test void naoPermiteCriarSacramentoDeMatrimonioPeloFluxoGenerico(){
        OrganizacaoEclesial o=new OrganizacaoEclesial(); o.setAtivo(true); UUID ou=UUID.randomUUID();
        when(organizacaoRepository.findByUuid(ou)).thenReturn(Optional.of(o));
        SacramentoApplicationService s=new SacramentoApplicationService(repository,organizacaoRepository);
        assertThrows(SacramentoBusinessException.class,()->s.criar(new SacramentoRequest(ou,TipoSacramento.MATRIMONIO,LocalDate.now(),null,null,null)));
    }
}
