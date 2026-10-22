package br.com.admeclesial.application.pessoa;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.pessoa.Familia;
import br.com.admeclesial.infrastructure.persistence.pessoa.FamiliaRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.MembroFamiliaRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.PessoaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class FamiliaApplicationService {

    private final FamiliaRepository repositorio;
    private final MembroFamiliaRepository membros;
    private final PessoaRepository pessoas;
    private final SegurancaOrganizacaoService seguranca;

    public FamiliaApplicationService(FamiliaRepository repositorio,
                                     MembroFamiliaRepository membros,
                                     PessoaRepository pessoas,
                                     SegurancaOrganizacaoService seguranca) {
        this.repositorio = repositorio;
        this.membros = membros;
        this.pessoas = pessoas;
        this.seguranca = seguranca;
    }

    @Transactional
    public Familia criar(FamiliaRequest x) {
        if (x.nome() == null || x.nome().isBlank()) throw new IllegalArgumentException("Nome da família é obrigatório.");
        var e = new Familia();
        e.setNome(x.nome().trim());
        e.setAtivo(x.ativo() == null || x.ativo());
        return repositorio.save(e);
    }

    @Transactional(readOnly = true)
    public List<Familia> listar() {
        var orgs = organizacoes();
        boolean admin = ehAdmin();
        return repositorio.findAll().stream().filter(f -> visivel(f.getId(), orgs, admin)).toList();
    }

    @Transactional(readOnly = true)
    public Familia buscar(UUID uuid) {
        var e = repositorio.findByUuid(uuid).orElseThrow(() -> new IllegalArgumentException("Família não encontrada."));
        validarVisibilidade(e);
        return e;
    }

    @Transactional
    public Familia atualizar(UUID uuid, FamiliaRequest x) {
        var e = repositorio.findByUuid(uuid).orElseThrow(() -> new IllegalArgumentException("Família não encontrada."));
        validarVisibilidade(e);
        if (x.nome() == null || x.nome().isBlank()) throw new IllegalArgumentException("Nome da família é obrigatório.");
        e.setNome(x.nome().trim());
        if (x.ativo() != null) e.setAtivo(x.ativo());
        return repositorio.save(e);
    }

    @Transactional
    public Familia inativar(UUID uuid) {
        var e = repositorio.findByUuid(uuid).orElseThrow(() -> new IllegalArgumentException("Família não encontrada."));
        validarVisibilidade(e);
        e.setAtivo(false);
        return repositorio.save(e);
    }

    @Transactional(readOnly = true)
    public boolean familiaVisivel(UUID familiaUuid) {
        var e = repositorio.findByUuid(familiaUuid).orElseThrow(() -> new IllegalArgumentException("Família não encontrada."));
        return visivel(e.getId(), organizacoes(), ehAdmin());
    }

    private void validarVisibilidade(Familia e) {
        if (!visivel(e.getId(), organizacoes(), ehAdmin())) {
            throw new AccessDeniedException("Família fora das organizações autorizadas");
        }
    }

    private boolean visivel(Long familiaId, Set<UUID> orgs, boolean admin) {
        if (admin) return true;
        if (orgs == null || orgs.isEmpty()) return false;
        var ms = membros.findByFamiliaId(familiaId);
        if (ms.isEmpty()) return false;
        return ms.stream().anyMatch(m -> pessoas.findById(m.getPessoaId())
                .map(p -> pessoas.isVisivel(p.getUuid(), orgs)).orElse(false));
    }

    private Set<UUID> organizacoes() {
        return seguranca == null ? Set.of() : seguranca.organizacoesAutorizadas();
    }

    private boolean ehAdmin() {
        return seguranca != null && seguranca.ehAdministrador();
    }
}
