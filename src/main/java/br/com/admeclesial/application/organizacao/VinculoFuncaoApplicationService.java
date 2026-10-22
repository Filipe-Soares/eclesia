package br.com.admeclesial.application.organizacao;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import br.com.admeclesial.domain.organizacao.VinculoFuncaoEclesial;
import br.com.admeclesial.infrastructure.persistence.organizacao.FuncaoEclesialRepository;
import br.com.admeclesial.infrastructure.persistence.organizacao.VinculoFuncaoRepository;
import br.com.admeclesial.infrastructure.persistence.organizacao.VinculoPessoaOrganizacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class VinculoFuncaoApplicationService {
    @Autowired private SegurancaOrganizacaoService seguranca;

    private final VinculoFuncaoRepository repository;
    private final VinculoPessoaOrganizacaoRepository vinculoRepository;
    private final FuncaoEclesialRepository funcaoRepository;

    public VinculoFuncaoApplicationService(VinculoFuncaoRepository repository,
                                           VinculoPessoaOrganizacaoRepository vinculoRepository,
                                           FuncaoEclesialRepository funcaoRepository) {
        this.repository = repository;
        this.vinculoRepository = vinculoRepository;
        this.funcaoRepository = funcaoRepository;
    }

    @Transactional
    public VinculoFuncaoResponse criar(VinculoFuncaoRequest r) {
        validarPeriodo(r.dataInicio(), r.dataFim());
        var vinculo = vinculoRepository.findByUuid(r.vinculoPessoaOrganizacaoUuid())
                .orElseThrow(() -> new VinculoNotFoundException(r.vinculoPessoaOrganizacaoUuid()));
        if (seguranca != null) seguranca.validarAcesso(vinculo.getOrganizacaoEclesial().getUuid());
        var funcao = funcaoRepository.findByUuid(r.funcaoEclesialUuid())
                .orElseThrow(() -> new FuncaoEclesialNotFoundException(r.funcaoEclesialUuid()));

        if (!funcao.isAtiva()) throw new VinculoBusinessException("Não é possível atribuir uma função eclesial inativa.");
        if (vinculo.getStatus() != br.com.admeclesial.domain.organizacao.StatusVinculoPessoaOrganizacao.ATIVO) {
            throw new VinculoBusinessException("Não é possível atribuir função a um vínculo pessoa-organização que não esteja ativo.");
        }
        if (r.dataInicio().isBefore(vinculo.getDataInicio()) ||
                (vinculo.getDataFim() != null && (r.dataFim() == null || r.dataFim().isAfter(vinculo.getDataFim())))) {
            throw new VinculoBusinessException("O período da função deve estar contido no período do vínculo pessoa-organização.");
        }
        if (repository.existsConflitoFuncaoNoVinculo(vinculo.getId(), funcao.getId(), r.dataInicio(), r.dataFim(), null)) {
            throw new VinculoBusinessException("A pessoa já possui esta mesma função no período informado neste vínculo.");
        }
        if (r.principal() && repository.existsPrincipalConflitante(vinculo.getId(), r.dataInicio(), r.dataFim(), null)) {
            throw new VinculoBusinessException("A pessoa já possui outra função principal no período informado neste vínculo.");
        }
        if (funcao.isExclusivoNaOrganizacao()
                && repository.existsConflitoFuncao(vinculo.getOrganizacaoEclesial().getId(), funcao.getId(), r.dataInicio(), r.dataFim(), null)) {
            throw new VinculoBusinessException("A função '" + funcao.getNome() + "' é exclusiva nesta organização e já está atribuída no período informado.");
        }

        var e = new VinculoFuncaoEclesial();
        e.setVinculoPessoaOrganizacao(vinculo);
        e.setFuncaoEclesial(funcao);
        e.setDataInicio(r.dataInicio());
        e.setDataFim(r.dataFim());
        e.setPrincipal(r.principal());
        e.setObservacao(text(r.observacao()));
        return toResponse(repository.save(e));
    }

    @Transactional(readOnly = true)
    public VinculoFuncaoResponse buscar(UUID uuid) {
        var e = repository.findByUuid(uuid).orElseThrow(() -> new VinculoNotFoundException(uuid));
        if (seguranca != null) seguranca.validarAcesso(e.getVinculoPessoaOrganizacao().getOrganizacaoEclesial().getUuid());
        return toResponse(e);
    }

    @Transactional(readOnly = true)
    public List<VinculoFuncaoResponse> listar() {
        var a = seguranca == null ? null : seguranca.organizacoesAutorizadas();
        return repository.findAllByOrderByDataInicioDesc().stream().filter(e -> a == null || a.contains(e.getVinculoPessoaOrganizacao().getOrganizacaoEclesial().getUuid())).map(this::toResponse).toList();
    }

    private VinculoFuncaoResponse toResponse(VinculoFuncaoEclesial e) {
        var v = e.getVinculoPessoaOrganizacao();
        var p = v.getPessoa();
        var o = v.getOrganizacaoEclesial();
        var f = e.getFuncaoEclesial();
        return new VinculoFuncaoResponse(e.getUuid(), v.getUuid(), p.getUuid(), p.getNome(), o.getUuid(), o.getNome(),
                f.getUuid(), f.getCodigo(), f.getNome(), e.getDataInicio(), e.getDataFim(), e.isPrincipal(), e.getObservacao());
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (fim != null && fim.isBefore(inicio)) throw new VinculoBusinessException("Data final não pode ser anterior à data inicial.");
    }

    private String text(String value) { return StringUtils.hasText(value) ? value.trim() : null; }
}
