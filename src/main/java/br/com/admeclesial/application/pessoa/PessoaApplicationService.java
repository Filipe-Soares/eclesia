package br.com.admeclesial.application.pessoa;

import br.com.admeclesial.domain.pessoa.Pessoa;
import br.com.admeclesial.infrastructure.persistence.pessoa.PessoaRepository;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PessoaApplicationService {

    private final PessoaRepository repository;
    private final SegurancaOrganizacaoService seguranca;

    @Autowired
    public PessoaApplicationService(PessoaRepository repository, @Autowired(required = false) SegurancaOrganizacaoService seguranca) {
        this.repository = repository;
        this.seguranca = seguranca;
    }

    public PessoaApplicationService(PessoaRepository repository) {
        this(repository, null);
    }

    @Transactional
    public PessoaResponse criar(PessoaRequest request) {
        String cpf = normalizarCpf(request.cpf());
        if (StringUtils.hasText(cpf) && repository.existsByCpf(cpf)) throw new PessoaBusinessException("CPF já cadastrado.");
        Pessoa pessoa = new Pessoa();
        aplicar(pessoa, request, cpf);
        return toResponse(repository.save(pessoa));
    }

    @Transactional
    public PessoaResponse atualizar(UUID uuid, PessoaRequest request) {
        var autorizadas = seguranca == null ? null : seguranca.organizacoesAutorizadas();
        if (autorizadas != null) {
            var orgs = autorizadas.isEmpty() ? Set.of(UUID.randomUUID()) : autorizadas;
            if (!repository.isVisivel(uuid, orgs)) {
                throw new AccessDeniedException("Pessoa fora das organizações autorizadas");
            }
        }
        Pessoa pessoa = repository.findByUuid(uuid).orElseThrow(() -> new PessoaNotFoundException(uuid));
        String cpf = normalizarCpf(request.cpf());
        if (StringUtils.hasText(cpf) && repository.existsByCpfAndUuidNot(cpf, uuid)) {
            throw new PessoaBusinessException("CPF já cadastrado para outra pessoa.");
        }
        aplicar(pessoa, request, cpf);
        return toResponse(repository.save(pessoa));
    }

    @Transactional(readOnly = true)
    public PessoaResponse buscar(UUID uuid) {
        var autorizadas = seguranca == null ? null : seguranca.organizacoesAutorizadas();
        if (autorizadas != null) {
            var orgs = autorizadas.isEmpty() ? Set.of(UUID.randomUUID()) : autorizadas;
            if (!repository.isVisivel(uuid, orgs)) {
                throw new AccessDeniedException("Pessoa fora das organizações autorizadas");
            }
        }
        return repository.findByUuid(uuid).map(this::toResponse).orElseThrow(() -> new PessoaNotFoundException(uuid));
    }

    @Transactional(readOnly = true)
    public List<PessoaResponse> listar() {
        var autorizadas = seguranca == null ? null : seguranca.organizacoesAutorizadas();
        if (autorizadas == null) return repository.findAllByOrderByNomeAsc().stream().map(this::toResponse).toList();
        var orgs = autorizadas.isEmpty() ? Set.of(UUID.randomUUID()) : autorizadas;
        return repository.findVisiveis(orgs).stream().map(this::toResponse).toList();
    }

    private void aplicar(Pessoa pessoa, PessoaRequest request, String cpf) {
        pessoa.setNome(request.nome().trim());
        pessoa.setNomeSocial(normalizarTexto(request.nomeSocial()));
        pessoa.setDataNascimento(request.dataNascimento());
        pessoa.setSexo(request.sexo());
        pessoa.setCpf(cpf);
        pessoa.setRg(normalizarTexto(request.rg()));
        pessoa.setEmail(normalizarTexto(request.email()));
        pessoa.setObservacao(normalizarTexto(request.observacao()));
    }

    private String normalizarCpf(String cpf) { return StringUtils.hasText(cpf) ? cpf.replaceAll("\\D", "") : null; }
    private String normalizarTexto(String value) { return StringUtils.hasText(value) ? value.trim() : null; }

    private PessoaResponse toResponse(Pessoa pessoa) {
        return new PessoaResponse(pessoa.getUuid(), pessoa.getNome(), pessoa.getNomeSocial(), pessoa.getDataNascimento(),
                pessoa.getSexo(), pessoa.getCpf(), pessoa.getRg(), pessoa.getEmail(), pessoa.getObservacao(),
                pessoa.isAtivo(), pessoa.getDataCadastro());
    }
}
