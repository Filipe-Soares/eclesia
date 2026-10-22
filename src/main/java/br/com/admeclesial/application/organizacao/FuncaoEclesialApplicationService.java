package br.com.admeclesial.application.organizacao;

import br.com.admeclesial.domain.organizacao.FuncaoEclesial;
import br.com.admeclesial.infrastructure.persistence.organizacao.FuncaoEclesialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class FuncaoEclesialApplicationService {
    private final FuncaoEclesialRepository repository;

    public FuncaoEclesialApplicationService(FuncaoEclesialRepository repository) { this.repository = repository; }

    @Transactional
    public FuncaoEclesialResponse criar(FuncaoEclesialRequest r) {
        String codigo = r.codigo().trim().toUpperCase();
        if (repository.existsByCodigo(codigo)) {
            throw new VinculoBusinessException("Já existe uma função eclesial com o código informado.");
        }
        var e = new FuncaoEclesial();
        e.setCodigo(codigo);
        e.setNome(r.nome().trim());
        e.setDescricao(text(r.descricao()));
        e.setExclusivoNaOrganizacao(r.exclusivoNaOrganizacao());
        return toResponse(repository.save(e));
    }

    @Transactional
    public FuncaoEclesialResponse atualizar(UUID uuid, FuncaoEclesialRequest r) {
        var e = repository.findByUuid(uuid).orElseThrow(() -> new FuncaoEclesialNotFoundException(uuid));
        String codigo = r.codigo().trim().toUpperCase();
        if (repository.existsByCodigoAndUuidNot(codigo, uuid)) {
            throw new VinculoBusinessException("Já existe uma função eclesial com o código informado.");
        }
        e.setCodigo(codigo);
        e.setNome(r.nome().trim());
        e.setDescricao(text(r.descricao()));
        e.setExclusivoNaOrganizacao(r.exclusivoNaOrganizacao());
        return toResponse(repository.save(e));
    }

    @Transactional(readOnly = true)
    public FuncaoEclesialResponse buscar(UUID uuid) {
        return repository.findByUuid(uuid).map(this::toResponse).orElseThrow(() -> new FuncaoEclesialNotFoundException(uuid));
    }

    @Transactional(readOnly = true)
    public List<FuncaoEclesialResponse> listar() {
        return repository.findAllByOrderByNomeAsc().stream().map(this::toResponse).toList();
    }

    private FuncaoEclesialResponse toResponse(FuncaoEclesial e) {
        return new FuncaoEclesialResponse(e.getUuid(), e.getCodigo(), e.getNome(), e.getDescricao(), e.isAtiva(), e.isExclusivoNaOrganizacao());
    }

    private String text(String value) { return StringUtils.hasText(value) ? value.trim() : null; }
}
