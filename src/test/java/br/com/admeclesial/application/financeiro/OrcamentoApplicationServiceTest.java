package br.com.admeclesial.application.financeiro;
import br.com.admeclesial.domain.financeiro.*;
import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.infrastructure.persistence.financeiro.*;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class OrcamentoApplicationServiceTest {
 @Mock OrcamentoRepository repo; @Mock ItemOrcamentoRepository itens; @Mock OrganizacaoEclesialRepository org; @Mock CategoriaFinanceiraRepository categorias; @Mock CentroCustoRepository centros;
 @Test void deveCriarOrcamentoPlanejado(){var o=new OrganizacaoEclesial();o.setAtivo(true);setId(o,1L);UUID ou=UUID.randomUUID();when(org.findByUuid(ou)).thenReturn(Optional.of(o));when(repo.findByOrganizacaoEclesialIdAndAno(1L,2027)).thenReturn(Optional.empty());when(repo.save(any())).thenAnswer(i->i.getArgument(0));var s=new OrcamentoApplicationService(repo,itens,org,categorias,centros);var out=s.criar(new OrcamentoRequest(ou,2027,"Orçamento 2027",null));assertEquals(StatusOrcamento.PLANEJADO,out.getStatus());assertEquals(2027,out.getAno());}
 @Test void naoPermiteAprovarSemItens(){var o=new Orcamento();setId(o,1L);o.setStatus(StatusOrcamento.PLANEJADO);UUID u=UUID.randomUUID();when(repo.findByUuid(u)).thenReturn(Optional.of(o));when(itens.findAllByOrcamentoIdOrderByIdAsc(1L)).thenReturn(List.of());var s=new OrcamentoApplicationService(repo,itens,org,categorias,centros);assertThrows(IllegalArgumentException.class,()->s.aprovar(u));}
 @Test void naoPermiteItemComCentroDeOutraOrganizacao(){var o=new Orcamento();setId(o,1L);o.setStatus(StatusOrcamento.PLANEJADO);o.setOrganizacaoEclesialId(10L);var c=new CategoriaFinanceira();c.setAtivo(true);var cc=new CentroCusto();cc.setAtivo(true);cc.setOrganizacaoEclesialId(20L);UUID ou=UUID.randomUUID(),cu=UUID.randomUUID(),ccu=UUID.randomUUID();when(repo.findByUuid(ou)).thenReturn(Optional.of(o));when(categorias.findByUuid(cu)).thenReturn(Optional.of(c));when(centros.findByUuid(ccu)).thenReturn(Optional.of(cc));var s=new OrcamentoApplicationService(repo,itens,org,categorias,centros);assertThrows(IllegalArgumentException.class,()->s.adicionarItem(ou,new ItemOrcamentoRequest(cu,ccu,"Item",new BigDecimal("100"))));}
 private static void setId(Object o,long id){try{var f=o.getClass().getDeclaredField("id");f.setAccessible(true);f.set(o,id);}catch(Exception e){throw new RuntimeException(e);}}
}
