package br.com.admeclesial.application.relatorio;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.infrastructure.persistence.relatorio.RelatorioCatequeseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service public class RelatorioCatequeseApplicationService {
 private final RelatorioCatequeseRepository repo; private final SegurancaOrganizacaoService seguranca;
 public RelatorioCatequeseApplicationService(RelatorioCatequeseRepository r,SegurancaOrganizacaoService s){repo=r;seguranca=s;}
 @Transactional(readOnly=true) public RelatorioCatequeseResumo consultar(RelatorioCatequeseFiltro f){
  if(f.organizacaoUuid()==null) throw new IllegalArgumentException("Organização é obrigatória"); seguranca.validarAcesso(f.organizacaoUuid());
  int tamanho=Math.max(10,Math.min(f.tamanho(),100)), pagina=Math.max(f.pagina(),0); String st=f.status()==null?null:f.status().name(),tu=f.turno()==null?null:f.turno().name();
  var page=repo.consultar(f.organizacaoUuid(),f.catequeseUuid(),f.etapaUuid(),f.ano(),st,tu,PageRequest.of(pagina,tamanho));
  var itens=page.getContent().stream().map(p->new RelatorioTurmaItem(p.getUuid(),p.getTurma(),p.getCatequese(),p.getEtapa(),p.getAno(),p.getTurno(),p.getStatus(),p.getCapacidade(),nz(p.getInscritos()),nz(p.getVagas()),p.getCatequistas())).toList();
  Object[] raw=repo.totais(f.organizacaoUuid(),f.catequeseUuid(),f.etapaUuid(),f.ano(),st,tu); Object[] t=(raw!=null&&raw.length==1&&raw[0] instanceof Object[] a)?a:raw;
  long turmas=num(t,0), inscritos=num(t,1), vagas=num(t,2);
  return new RelatorioCatequeseResumo(turmas,inscritos,vagas,new RelatorioPagina<>(itens,pagina,tamanho,page.getTotalElements(),page.getTotalPages(),"turma","asc"));
 }
 @Transactional(readOnly=true) public List<RelatorioOpcao> catequeses(UUID org){if(org==null)return List.of();seguranca.validarAcesso(org);return repo.catequeses(org);}
 @Transactional(readOnly=true) public List<RelatorioOpcao> etapas(UUID org,UUID catequese){if(catequese==null)return List.of();seguranca.validarAcesso(org);return repo.etapas(catequese);}
 private static long nz(Long n){return n==null?0:n;} private static long num(Object[] a,int i){return a!=null&&a.length>i&&a[i] instanceof Number n?n.longValue():0;}
}
