package br.com.admeclesial.application.catequese;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService; import org.springframework.beans.factory.annotation.Autowired;
import br.com.admeclesial.domain.catequese.*; import br.com.admeclesial.infrastructure.persistence.catequese.CatequeseRepository; import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import org.springframework.util.StringUtils; import java.time.LocalDate; import java.util.*;
@Service public class CatequeseApplicationService {
 @Autowired private SegurancaOrganizacaoService seguranca;
 private final CatequeseRepository repository; private final OrganizacaoEclesialRepository organizacaoRepository;
 public CatequeseApplicationService(CatequeseRepository r, OrganizacaoEclesialRepository o){repository=r;organizacaoRepository=o;}
 @Transactional public CatequeseResponse criar(CatequeseRequest r){validarAcesso(r.organizacaoEclesialUuid());validarPeriodo(r.dataInicio(),r.dataFim()); var org=organizacaoRepository.findByUuid(r.organizacaoEclesialUuid()).orElseThrow(()->new CatequeseBusinessException("Organização eclesial não encontrada.")); if(!org.isAtivo())throw new CatequeseBusinessException("A organização eclesial está inativa."); var e=new Catequese(); e.setOrganizacaoEclesial(org); e.setNome(r.nome().trim()); e.setDescricao(text(r.descricao())); e.setStatus(r.status()==null?StatusCatequese.PLANEJADA:r.status()); e.setDataInicio(r.dataInicio()); e.setDataFim(r.dataFim()); e.setObservacao(text(r.observacao())); return toResponse(repository.save(e)); }
 @Transactional public CatequeseResponse atualizar(UUID uuid, CatequeseRequest r){
  validarPeriodo(r.dataInicio(),r.dataFim());
  var e=repository.findByUuid(uuid).orElseThrow(()->new CatequeseNotFoundException(uuid));
  validarAcesso(e.getOrganizacaoEclesial().getUuid());
  validarAcesso(r.organizacaoEclesialUuid());
  var org=organizacaoRepository.findByUuid(r.organizacaoEclesialUuid()).orElseThrow(()->new CatequeseBusinessException("Organização eclesial não encontrada."));
  if(!org.isAtivo())throw new CatequeseBusinessException("A organização eclesial está inativa.");
  e.setOrganizacaoEclesial(org); e.setNome(r.nome().trim()); e.setDescricao(text(r.descricao()));
  e.setStatus(r.status()==null?StatusCatequese.PLANEJADA:r.status()); e.setDataInicio(r.dataInicio()); e.setDataFim(r.dataFim()); e.setObservacao(text(r.observacao()));
  return toResponse(repository.save(e));
 }
 @Transactional(readOnly=true) public CatequeseResponse buscar(UUID uuid){var e=repository.findByUuid(uuid).orElseThrow(()->new CatequeseNotFoundException(uuid));validarAcesso(e.getOrganizacaoEclesial().getUuid());return toResponse(e);}
 @Transactional(readOnly=true) public List<CatequeseResponse> listar(){var a=seguranca==null?null:seguranca.organizacoesAutorizadas();return repository.findAllByOrderByNomeAsc().stream().filter(e->a==null||a.contains(e.getOrganizacaoEclesial().getUuid())).map(this::toResponse).toList();}
 private CatequeseResponse toResponse(Catequese e){var o=e.getOrganizacaoEclesial();return new CatequeseResponse(e.getUuid(),o.getUuid(),o.getNome(),e.getNome(),e.getDescricao(),e.getStatus(),e.getDataInicio(),e.getDataFim(),e.getObservacao());}
 private void validarPeriodo(LocalDate i,LocalDate f){if(f!=null&&i==null)throw new CatequeseBusinessException("Data inicial é obrigatória quando a data final for informada.");if(i!=null&&f!=null&&f.isBefore(i))throw new CatequeseBusinessException("Data final não pode ser anterior à data inicial.");}
 private String text(String v){return StringUtils.hasText(v)?v.trim():null;}

 private void validarAcesso(java.util.UUID uuid){if(seguranca!=null)seguranca.validarAcesso(uuid);}
}
