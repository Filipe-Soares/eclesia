package br.com.admeclesial.application.organizacao;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import br.com.admeclesial.domain.organizacao.VinculoPessoaOrganizacaoEclesial;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import br.com.admeclesial.infrastructure.persistence.organizacao.VinculoPessoaOrganizacaoRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.PessoaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class VinculoPessoaOrganizacaoApplicationService {
    @Autowired private SegurancaOrganizacaoService seguranca;

    private final VinculoPessoaOrganizacaoRepository repository;
    private final PessoaRepository pessoaRepository;
    private final OrganizacaoEclesialRepository organizacaoRepository;

    public VinculoPessoaOrganizacaoApplicationService(VinculoPessoaOrganizacaoRepository repository,
                                                      PessoaRepository pessoaRepository,
                                                      OrganizacaoEclesialRepository organizacaoRepository) {
        this.repository = repository;
        this.pessoaRepository = pessoaRepository;
        this.organizacaoRepository = organizacaoRepository;
    }

    @Transactional
    public VinculoPessoaOrganizacaoResponse criar(VinculoPessoaOrganizacaoRequest r) {
        validarPeriodo(r.dataInicio(), r.dataFim());
        if (seguranca != null) seguranca.validarAcesso(r.organizacaoUuid());
        var pessoa = pessoaRepository.findByUuid(r.pessoaUuid())
                .orElseThrow(() -> new br.com.admeclesial.application.pessoa.PessoaNotFoundException(r.pessoaUuid()));
        var organizacao = organizacaoRepository.findByUuid(r.organizacaoUuid())
                .orElseThrow(() -> new OrganizacaoNotFoundException(r.organizacaoUuid()));

        if (!pessoa.isAtivo()) {
            throw new VinculoBusinessException("Não é possível criar vínculo para uma pessoa inativa.");
        }
        if (!organizacao.isAtivo()) {
            throw new VinculoBusinessException("Não é possível criar vínculo com uma organização eclesial inativa.");
        }

        if (r.principal() && r.status() == br.com.admeclesial.domain.organizacao.StatusVinculoPessoaOrganizacao.ATIVO
                && repository.existsPrincipalConflitante(pessoa.getId(), organizacao.getId(), r.dataInicio(), r.dataFim(), null)) {
            throw new VinculoBusinessException("A pessoa já possui outro vínculo principal ativo com esta organização no período informado.");
        }

        var e = new VinculoPessoaOrganizacaoEclesial();
        e.setPessoa(pessoa);
        e.setOrganizacaoEclesial(organizacao);
        e.setDataInicio(r.dataInicio());
        e.setDataFim(r.dataFim());
        e.setStatus(r.status());
        e.setPrincipal(r.principal());
        e.setObservacao(text(r.observacao()));
        return toResponse(repository.save(e));
    }

    @Transactional
    public VinculoPessoaOrganizacaoResponse atualizar(UUID uuid, VinculoPessoaOrganizacaoRequest r) {
        validarPeriodo(r.dataInicio(), r.dataFim());
        var e = repository.findByUuid(uuid).orElseThrow(() -> new VinculoNotFoundException(uuid));
        if (seguranca != null) {
            seguranca.validarAcesso(e.getOrganizacaoEclesial().getUuid());
            seguranca.validarAcesso(r.organizacaoUuid());
        }

        var pessoa = pessoaRepository.findByUuid(r.pessoaUuid())
                .orElseThrow(() -> new br.com.admeclesial.application.pessoa.PessoaNotFoundException(r.pessoaUuid()));
        var organizacao = organizacaoRepository.findByUuid(r.organizacaoUuid())
                .orElseThrow(() -> new OrganizacaoNotFoundException(r.organizacaoUuid()));

        if (!pessoa.isAtivo()) {
            throw new VinculoBusinessException("Não é possível vincular uma pessoa inativa.");
        }
        if (!organizacao.isAtivo()) {
            throw new VinculoBusinessException("Não é possível vincular uma organização eclesial inativa.");
        }

        if (r.principal() && r.status() == br.com.admeclesial.domain.organizacao.StatusVinculoPessoaOrganizacao.ATIVO
                && repository.existsPrincipalConflitante(pessoa.getId(), organizacao.getId(), r.dataInicio(), r.dataFim(), uuid)) {
            throw new VinculoBusinessException("A pessoa já possui outro vínculo principal ativo com esta organização no período informado.");
        }

        e.setPessoa(pessoa);
        e.setOrganizacaoEclesial(organizacao);
        e.setDataInicio(r.dataInicio());
        e.setDataFim(r.dataFim());
        e.setStatus(r.status());
        e.setPrincipal(r.principal());
        e.setObservacao(text(r.observacao()));
        return toResponse(repository.save(e));
    }

    @Transactional(readOnly = true)
    public VinculoPessoaOrganizacaoResponse buscar(UUID uuid) {
        var e = repository.findByUuid(uuid).orElseThrow(() -> new VinculoNotFoundException(uuid));
        if (seguranca != null) seguranca.validarAcesso(e.getOrganizacaoEclesial().getUuid());
        return toResponse(e);
    }

    @Transactional(readOnly = true)
    public List<VinculoPessoaOrganizacaoResponse> listar() {
        var a = seguranca == null ? null : seguranca.organizacoesAutorizadas();
        return repository.findAllByOrderByDataInicioDesc().stream().filter(e -> a == null || a.contains(e.getOrganizacaoEclesial().getUuid())).map(this::toResponse).toList();
    }

    private VinculoPessoaOrganizacaoResponse toResponse(VinculoPessoaOrganizacaoEclesial e) {
        return new VinculoPessoaOrganizacaoResponse(e.getUuid(), e.getPessoa().getUuid(), e.getPessoa().getNome(),
                e.getOrganizacaoEclesial().getUuid(), e.getOrganizacaoEclesial().getNome(), e.getDataInicio(),
                e.getDataFim(), e.getStatus(), e.isPrincipal(), e.getObservacao());
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (fim != null && fim.isBefore(inicio)) throw new VinculoBusinessException("Data final não pode ser anterior à data inicial.");
    }

    private String text(String value) { return StringUtils.hasText(value) ? value.trim() : null; }
}
