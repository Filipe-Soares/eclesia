package br.com.admeclesial.application.organizacao;

import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizacaoEclesialApplicationService {

    private final OrganizacaoEclesialRepository repository;
    private final SegurancaOrganizacaoService seguranca;

    @Autowired
    public OrganizacaoEclesialApplicationService(
            OrganizacaoEclesialRepository repository,
            @Autowired(required = false) SegurancaOrganizacaoService seguranca) {
        this.repository = repository;
        this.seguranca = seguranca;
    }

    public OrganizacaoEclesialApplicationService(OrganizacaoEclesialRepository repository) {
        this(repository, null);
    }

    @Transactional
    public OrganizacaoResponse criar(OrganizacaoRequest r) {
        var e = new OrganizacaoEclesial();
        aplicar(e, r);
        return toResponse(repository.save(e));
    }

    @Transactional
    public OrganizacaoResponse atualizar(UUID uuid, OrganizacaoRequest r) {
        if (seguranca != null) seguranca.validarAcesso(uuid);
        var e = repository.findByUuid(uuid).orElseThrow(() -> new OrganizacaoNotFoundException(uuid));
        aplicar(e, r);
        return toResponse(repository.save(e));
    }

    private void aplicar(OrganizacaoEclesial e, OrganizacaoRequest r) {
        e.setNome(r.nome().trim());
        e.setNomeReduzido(text(r.nomeReduzido()));
        e.setTipo(r.tipo());
        e.setCnpj(digits(r.cnpj()));
        e.setEmail(text(r.email()));
        e.setTelefone(text(r.telefone()));
        e.setObservacao(text(r.observacao()));
    }

    @Transactional(readOnly = true)
    public OrganizacaoResponse buscar(UUID uuid) {
        if(seguranca!=null) seguranca.validarAcesso(uuid);
        return repository.findByUuid(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new OrganizacaoNotFoundException(uuid));
    }

    @Transactional(readOnly = true)
    public List<OrganizacaoResponse> listar() {
        var autorizadas = seguranca == null ? null : seguranca.organizacoesAutorizadas();
        return repository.findAllByOrderByNomeAsc().stream()
                .filter(o -> autorizadas == null || autorizadas.contains(o.getUuid())).map(this::toResponse).toList();
    }

    private OrganizacaoResponse toResponse(OrganizacaoEclesial e) {
        return new OrganizacaoResponse(e.getUuid(), e.getNome(), e.getNomeReduzido(), e.getTipo(),
                e.getCnpj(), e.getEmail(), e.getTelefone(), e.isAtivo(), e.getDataCadastro(), e.getObservacao());
    }

    private String text(String value) { return StringUtils.hasText(value) ? value.trim() : null; }
    private String digits(String value) { return StringUtils.hasText(value) ? value.replaceAll("\\D", "") : null; }
}
