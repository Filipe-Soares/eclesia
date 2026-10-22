package br.com.admeclesial.application.catequese;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService; import org.springframework.beans.factory.annotation.Autowired;
import br.com.admeclesial.domain.catequese.EtapaCatequese;
import br.com.admeclesial.infrastructure.persistence.catequese.CatequeseRepository;
import br.com.admeclesial.infrastructure.persistence.catequese.EtapaCatequeseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class EtapaCatequeseApplicationService {
    @Autowired private SegurancaOrganizacaoService seguranca;
    private final EtapaCatequeseRepository repository;
    private final CatequeseRepository catequeseRepository;

    public EtapaCatequeseApplicationService(EtapaCatequeseRepository repository,
                                             CatequeseRepository catequeseRepository) {
        this.repository = repository;
        this.catequeseRepository = catequeseRepository;
    }

    @Transactional
    public EtapaCatequeseResponse criar(EtapaCatequeseRequest request) {
        var catequese = catequeseRepository.findByUuid(request.catequeseUuid())
                .orElseThrow(() -> new CatequeseBusinessException("Catequese não encontrada."));
        validarAcesso(catequese.getOrganizacaoEclesial().getUuid());
        validarUnicidade(catequese.getId(), request.nome(), request.ordem(), null);

        var etapa = new EtapaCatequese();
        etapa.setCatequese(catequese);
        aplicar(etapa, request);
        return toResponse(repository.save(etapa));
    }

    @Transactional
    public EtapaCatequeseResponse atualizar(UUID uuid, EtapaCatequeseRequest request) {
        var etapa = repository.findByUuid(uuid)
                .orElseThrow(() -> new CatequeseBusinessException("Etapa de catequese não encontrada."));
        validarAcesso(etapa.getCatequese().getOrganizacaoEclesial().getUuid());
        var catequese = catequeseRepository.findByUuid(request.catequeseUuid())
                .orElseThrow(() -> new CatequeseBusinessException("Catequese não encontrada."));
        validarAcesso(catequese.getOrganizacaoEclesial().getUuid());
        validarUnicidade(catequese.getId(), request.nome(), request.ordem(), etapa.getId());

        etapa.setCatequese(catequese);
        aplicar(etapa, request);
        return toResponse(repository.save(etapa));
    }

    @Transactional(readOnly = true)
    public EtapaCatequeseResponse buscar(UUID uuid) {
        var etapa = repository.findByUuid(uuid).orElseThrow(() -> new CatequeseBusinessException("Etapa de catequese não encontrada."));
        validarAcesso(etapa.getCatequese().getOrganizacaoEclesial().getUuid());
        return toResponse(etapa);
    }

    @Transactional(readOnly = true)
    public List<EtapaCatequeseResponse> listar(UUID catequeseUuid) {
        var catequese = catequeseRepository.findByUuid(catequeseUuid)
                .orElseThrow(() -> new CatequeseBusinessException("Catequese não encontrada."));
        validarAcesso(catequese.getOrganizacaoEclesial().getUuid());
        return repository.findAllByCatequeseIdOrderByOrdemAsc(catequese.getId()).stream()
                .map(this::toResponse).toList();
    }

    private void validarUnicidade(Long catequeseId, String nome, Integer ordem, Long etapaId) {
        String nomeNormalizado = nome.trim();
        boolean nomeDuplicado = etapaId == null
                ? repository.existsByCatequeseIdAndNomeIgnoreCase(catequeseId, nomeNormalizado)
                : repository.existsByCatequeseIdAndNomeIgnoreCaseAndIdNot(catequeseId, nomeNormalizado, etapaId);
        if (nomeDuplicado) {
            throw new CatequeseBusinessException("Já existe uma etapa com esse nome nesta catequese.");
        }

        boolean ordemDuplicada = etapaId == null
                ? repository.existsByCatequeseIdAndOrdem(catequeseId, ordem)
                : repository.existsByCatequeseIdAndOrdemAndIdNot(catequeseId, ordem, etapaId);
        if (ordemDuplicada) {
            throw new CatequeseBusinessException("Já existe uma etapa com essa ordem nesta catequese.");
        }
    }

    private void aplicar(EtapaCatequese etapa, EtapaCatequeseRequest request) {
        etapa.setNome(request.nome().trim());
        etapa.setDescricao(text(request.descricao()));
        etapa.setOrdem(request.ordem());
        etapa.setAtivo(request.ativo() == null || request.ativo());
    }

    private EtapaCatequeseResponse toResponse(EtapaCatequese etapa) {
        return new EtapaCatequeseResponse(
                etapa.getUuid(),
                etapa.getCatequese().getUuid(),
                etapa.getCatequese().getNome(),
                etapa.getNome(),
                etapa.getDescricao(),
                etapa.getOrdem(),
                etapa.isAtivo());
    }

    private String text(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

 private void validarAcesso(java.util.UUID uuid){if(seguranca!=null)seguranca.validarAcesso(uuid);}
}
