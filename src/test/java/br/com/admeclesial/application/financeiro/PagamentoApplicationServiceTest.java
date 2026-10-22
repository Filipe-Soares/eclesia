package br.com.admeclesial.application.financeiro;
import br.com.admeclesial.domain.financeiro.*; import br.com.admeclesial.infrastructure.persistence.financeiro.*; import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.*; import static org.junit.jupiter.api.Assertions.*; import static org.mockito.ArgumentMatchers.*; import static org.mockito.Mockito.*;
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class) class PagamentoApplicationServiceTest {
 @Mock PagamentoRepository repo; @Mock ContaFinanceiraRepository contas; @Mock ParcelaAPagarRepository parcelas; @Mock AlocacaoPagamentoRepository alocacoes; @Mock PagamentoMovimentacaoRepository links; @Mock MovimentacaoFinanceiraRepository movimentos; @Mock ContaAPagarRepository contasPagar;
 @Test void deveAlocarPagamentoEGerarSaida(){
  var conta=new ContaFinanceira();conta.setAtivo(true);conta.setOrganizacaoEclesialId(1L);var cp=new ContaAPagar();cp.setOrganizacaoEclesialId(1L);var p=new ParcelaAPagar();try{var f=ContaAPagar.class.getDeclaredField("id");f.setAccessible(true);f.set(cp,1L);f=ParcelaAPagar.class.getDeclaredField("id");f.setAccessible(true);f.set(p,2L);}catch(Exception e){throw new RuntimeException(e);}p.setContaAPagarId(1L);p.setValor(new BigDecimal("100"));p.setStatus(StatusParcelaAPagar.ABERTA);
  UUID cu=UUID.randomUUID(),pu=UUID.randomUUID();when(contas.findByUuid(cu)).thenReturn(Optional.of(conta));when(parcelas.findByUuid(pu)).thenReturn(Optional.of(p));when(contasPagar.findById(1L)).thenReturn(Optional.of(cp));when(alocacoes.sumValorPorParcela(anyLong())).thenReturn(BigDecimal.ZERO, new BigDecimal("100"));when(alocacoes.save(any())).thenAnswer(i->i.getArgument(0));when(parcelas.save(any())).thenAnswer(i->i.getArgument(0));when(contasPagar.save(any())).thenAnswer(i->i.getArgument(0));when(repo.save(any())).thenAnswer(i->i.getArgument(0));when(movimentos.save(any())).thenAnswer(i->i.getArgument(0));when(links.save(any())).thenAnswer(i->i.getArgument(0));when(parcelas.findAllByContaAPagarIdOrderByNumeroAsc(1L)).thenReturn(List.of(p));
  var s=new PagamentoApplicationService(repo,contas,parcelas,alocacoes,links,movimentos,contasPagar);var out=s.criar(new PagamentoRequest(cu,new BigDecimal("100"),LocalDate.now(),MeioPagamento.PIX,List.of(new AlocacaoPagamentoRequest(pu,new BigDecimal("100"))),null));
  assertEquals(new BigDecimal("100"),out.getValor());assertEquals(StatusParcelaAPagar.PAGA,p.getStatus());verify(movimentos).save(argThat(x->x.getTipo()==TipoMovimentacaoFinanceira.SAIDA));verify(links).save(any());assertEquals(StatusContaAPagar.PAGA,cp.getStatus());
 }
 @Test void naoPagaContaCancelada(){
  var conta=new ContaFinanceira();conta.setAtivo(true);conta.setOrganizacaoEclesialId(1L);var cp=new ContaAPagar();cp.setOrganizacaoEclesialId(1L);cp.setStatus(StatusContaAPagar.CANCELADA);var p=new ParcelaAPagar();setId(cp,1L);setId(p,2L);p.setContaAPagarId(1L);p.setValor(new BigDecimal("100"));p.setStatus(StatusParcelaAPagar.ABERTA);UUID cu=UUID.randomUUID(),pu=UUID.randomUUID();when(contas.findByUuid(cu)).thenReturn(Optional.of(conta));when(parcelas.findByUuid(pu)).thenReturn(Optional.of(p));when(contasPagar.findById(1L)).thenReturn(Optional.of(cp));var s=new PagamentoApplicationService(repo,contas,parcelas,alocacoes,links,movimentos,contasPagar);assertThrows(IllegalArgumentException.class,()->s.criar(new PagamentoRequest(cu,new BigDecimal("100"),LocalDate.now(),MeioPagamento.PIX,List.of(new AlocacaoPagamentoRequest(pu,new BigDecimal("100"))),null)));
 }

 private static void setId(Object o,long id){try{var f=o.getClass().getDeclaredField("id");f.setAccessible(true);f.set(o,id);}catch(Exception e){throw new RuntimeException(e);}}
}

