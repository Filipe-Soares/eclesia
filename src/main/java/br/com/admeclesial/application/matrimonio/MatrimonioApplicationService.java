package br.com.admeclesial.application.matrimonio;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import br.com.admeclesial.domain.matrimonio.*;
import br.com.admeclesial.domain.sacramento.*;
import br.com.admeclesial.infrastructure.persistence.matrimonio.*;
import br.com.admeclesial.infrastructure.persistence.sacramento.ParticipanteSacramentoRepository;
import br.com.admeclesial.infrastructure.persistence.sacramento.SacramentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDate;
import java.util.*;

@Service
public class MatrimonioApplicationService {
    @Autowired SegurancaOrganizacaoService seguranca;
    final MatrimonioRepository repo;
    final ProcessoMatrimonialRepository proc;
    final CelebracaoMatrimonialRepository cele;
    final SacramentoRepository sacramentos;
    final ParticipanteSacramentoRepository participantes;

    public MatrimonioApplicationService(MatrimonioRepository r, ProcessoMatrimonialRepository p,
                                         CelebracaoMatrimonialRepository c, SacramentoRepository s,
                                         ParticipanteSacramentoRepository ps) {
        repo=r; proc=p; cele=c; sacramentos=s; participantes=ps;
    }

    @Transactional
    public MatrimonioResponse salvar(MatrimonioRequest r) {
        ProcessoMatrimonial p=proc.findByUuid(r.processoUuid())
                .orElseThrow(()->new IllegalArgumentException("Processo matrimonial não encontrado."));
        validar(p.getOrganizacao().getUuid());
        CelebracaoMatrimonial c=cele.findByUuid(r.celebracaoUuid())
                .orElseThrow(()->new IllegalArgumentException("Celebração matrimonial não encontrada."));
        if(!mesmaEntidade(c.getProcesso(),p))
            throw new IllegalArgumentException("A celebração não pertence ao processo.");
        if(!mesmaEntidade(c.getOrganizacao(),p.getOrganizacao()))
            throw new IllegalArgumentException("A organização da celebração deve ser a mesma organização do processo matrimonial.");
        if(p.getStatus()==StatusProcessoMatrimonial.CANCELADO)
            throw new IllegalArgumentException("Processo cancelado não pode gerar matrimônio.");
        if(r.dataRegistro().isBefore(c.getDataCelebracao()))
            throw new IllegalArgumentException("Data de registro não pode ser anterior à celebração.");

        Matrimonio e=repo.findByProcessoId(p.getId()).orElseGet(Matrimonio::new);
        e.setProcesso(p);
        e.setCelebracao(c);
        e.setDataRegistro(r.dataRegistro());
        e.setObservacao(StringUtils.hasText(r.observacao())?r.observacao().trim():null);

        if(e.getSacramento()==null) {
            Sacramento s=new Sacramento();
            s.setOrganizacaoEclesial(p.getOrganizacao());
            s.setTipo(TipoSacramento.MATRIMONIO);
            s.setDataCelebracao(c.getDataCelebracao());
            s.setLocal(c.getLocal());
            s.setStatus(StatusSacramento.REGISTRADO);
            s.setObservacao(e.getObservacao());
            s=sacramentos.save(s);
            e.setSacramento(s);
            criarParticipante(s,p.getNubente1(),TipoParticipacaoSacramento.CONJUGE);
            criarParticipante(s,p.getNubente2(),TipoParticipacaoSacramento.CONJUGE);
        }

        p.setStatus(StatusProcessoMatrimonial.CONCLUIDO);
        if(p.getDataEncerramento()==null) p.setDataEncerramento(r.dataRegistro());
        return out(repo.save(e));
    }

    private boolean mesmaEntidade(Object a,Object b){ if(a==b)return true; if(a instanceof ProcessoMatrimonial pa && b instanceof ProcessoMatrimonial pb)return pa.getId()!=null && pb.getId()!=null && pa.getId().equals(pb.getId()); if(a instanceof br.com.admeclesial.domain.organizacao.OrganizacaoEclesial oa && b instanceof br.com.admeclesial.domain.organizacao.OrganizacaoEclesial ob)return oa.getId()!=null && ob.getId()!=null && oa.getId().equals(ob.getId()); return false; }

    private void criarParticipante(Sacramento s, br.com.admeclesial.domain.pessoa.Pessoa pessoa, TipoParticipacaoSacramento tipo) {
        ParticipanteSacramento x=new ParticipanteSacramento();
        x.setSacramento(s); x.setPessoa(pessoa); x.setTipoParticipacao(tipo);
        participantes.save(x);
    }

    @Transactional(readOnly=true)
    public MatrimonioResponse buscar(UUID u){var e=repo.findByUuid(u).orElseThrow(()->new IllegalArgumentException("Matrimônio não encontrado."));validar(e.getProcesso().getOrganizacao().getUuid());return out(e);}

    @Transactional(readOnly=true)
    public MatrimonioResponse porProcesso(UUID u){
        var p=proc.findByUuid(u).orElseThrow(()->new IllegalArgumentException("Processo matrimonial não encontrado."));
        validar(p.getOrganizacao().getUuid());
        return repo.findByProcessoId(p.getId()).map(this::out).orElseThrow(()->new IllegalArgumentException("Matrimônio não encontrado."));
    }

    private MatrimonioResponse out(Matrimonio e){return new MatrimonioResponse(e.getUuid(),e.getProcesso().getUuid(),e.getCelebracao().getUuid(),e.getSacramento()==null?null:e.getSacramento().getUuid(),e.getDataRegistro(),e.getObservacao());}
    private void validar(UUID u){if(seguranca!=null)seguranca.validarAcesso(u);}
}
