package br.com.admeclesial.application.financeiro;

import br.com.admeclesial.domain.financeiro.*;
import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.infrastructure.persistence.financeiro.*;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.PessoaRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.*;
import static org.junit.jupiter.api.Assertions.*; import static org.mockito.ArgumentMatchers.*; import static org.mockito.Mockito.*;
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class) class ContribuicaoApplicationServiceTest {
 @Mock ContribuicaoRepository repo; @Mock OrganizacaoEclesialRepository org; @Mock PessoaRepository pessoas; @Mock CategoriaFinanceiraRepository categorias; @Mock ContaFinanceiraRepository contas; @Mock DizimistaRepository dizimistas; @Mock MovimentacaoFinanceiraRepository movimentos; @Mock ContribuicaoMovimentacaoRepository links;
 @Test void deveGerarMovimentacaoAoRegistrarContribuicao(){
  var o=new OrganizacaoEclesial();o.setAtivo(true);try{var f=OrganizacaoEclesial.class.getDeclaredField("id");f.setAccessible(true);f.set(o,1L);}catch(Exception e){throw new RuntimeException(e);}var conta=new ContaFinanceira();conta.setAtivo(true);conta.setOrganizacaoEclesialId(1L);var cat=new CategoriaFinanceira();cat.setAtivo(true);
  UUID ou=UUID.randomUUID(),cu=UUID.randomUUID(),catu=UUID.randomUUID();when(org.findByUuid(ou)).thenReturn(Optional.of(o));when(contas.findByUuid(cu)).thenReturn(Optional.of(conta));when(categorias.findByUuid(catu)).thenReturn(Optional.of(cat));
  when(repo.save(any())).thenAnswer(i->{var e=(Contribuicao)i.getArgument(0);return e;});when(movimentos.save(any())).thenAnswer(i->{var e=(MovimentacaoFinanceira)i.getArgument(0);return e;});when(links.save(any())).thenAnswer(i->i.getArgument(0));
  var s=new ContribuicaoApplicationService(repo,org,pessoas,categorias,contas,dizimistas,movimentos,links);
  var out=s.salvar(new ContribuicaoRequest(ou,cu,null,null,catu,TipoContribuicao.DOACAO,new BigDecimal("100"),LocalDate.of(2026,9,1),null,MeioRecebimento.PIX,null));
  assertEquals(new BigDecimal("100"),out.getValor());verify(movimentos).save(argThat(x->x.getTipo()==TipoMovimentacaoFinanceira.ENTRADA && x.getValor().compareTo(new BigDecimal("100"))==0));verify(links).save(any());
 }
 @Test void naoAceitaCategoriaInativa(){
  var o=new OrganizacaoEclesial();o.setAtivo(true);setId(o,1L);UUID ou=UUID.randomUUID(),cu=UUID.randomUUID(),catu=UUID.randomUUID();
  var conta=new ContaFinanceira();conta.setAtivo(true);conta.setOrganizacaoEclesialId(1L);var cat=new CategoriaFinanceira();cat.setAtivo(true);cat.setAtivo(false);
  when(org.findByUuid(ou)).thenReturn(Optional.of(o));when(contas.findByUuid(cu)).thenReturn(Optional.of(conta));when(categorias.findByUuid(catu)).thenReturn(Optional.of(cat));
  var s=new ContribuicaoApplicationService(repo,org,pessoas,categorias,contas,dizimistas,movimentos,links);
  assertThrows(IllegalArgumentException.class,()->s.salvar(new ContribuicaoRequest(ou,cu,null,null,catu,TipoContribuicao.DOACAO,new BigDecimal("10"),LocalDate.now(),null,MeioRecebimento.PIX,null)));
 }

 @Test void dizimoExigeDizimista(){
  var o=new OrganizacaoEclesial();o.setAtivo(true);UUID ou=UUID.randomUUID();when(org.findByUuid(ou)).thenReturn(Optional.of(o));
  var s=new ContribuicaoApplicationService(repo,org,pessoas,categorias,contas,dizimistas,movimentos,links);
  assertThrows(IllegalArgumentException.class,()->s.salvar(new ContribuicaoRequest(ou,UUID.randomUUID(),null, null,UUID.randomUUID(),TipoContribuicao.DIZIMO,new BigDecimal("10"),LocalDate.now(),null,MeioRecebimento.PIX,null)));
 }
 private static void setId(Object o,long id){try{var f=o.getClass().getDeclaredField("id");f.setAccessible(true);f.set(o,id);}catch(Exception e){throw new RuntimeException(e);}}
}

