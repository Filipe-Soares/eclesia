package br.com.admeclesial.application.pessoa;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.pessoa.MembroFamilia;
import br.com.admeclesial.domain.pessoa.Pessoa;
import br.com.admeclesial.infrastructure.persistence.pessoa.FamiliaRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.MembroFamiliaRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.PessoaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class MembroFamiliaApplicationService {

    private final MembroFamiliaRepository repositorio;
    private final FamiliaRepository familias;
    private final PessoaRepository pessoas;
    private final SegurancaOrganizacaoService seguranca;

    public MembroFamiliaApplicationService(MembroFamiliaRepository repositorio,
                                           FamiliaRepository familias,
                                           PessoaRepository pessoas,
                                           SegurancaOrganizacaoService seguranca) {
        this.repositorio = repositorio;
        this.familias = familias;
        this.pessoas = pessoas;
        this.seguranca = seguranca;
    }

    @Transactional
    public MembroFamilia criar(MembroFamiliaRequest x) {
        var fam = familias.findByUuid(x.familiaUuid()).orElseThrow(() -> new IllegalArgumentException("Família não encontrada."));
        var pes = pessoas.findByUuid(x.pessoaUuid()).orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada."));
        validarPessoa(x.pessoaUuid());
        if (!fam.isAtivo() || !pes.isAtivo()) throw new IllegalArgumentException("Família e pessoa devem estar ativas.");
        if (x.dataInicio() == null) throw new IllegalArgumentException("Data de início é obrigatória.");
        if (x.dataFim() != null && x.dataFim().isBefore(x.dataInicio())) throw new IllegalArgumentException("Período inválido.");
        if (repositorio.existsByFamiliaIdAndPessoaIdAndDataFimIsNull(fam.getId(), pes.getId())) {
            throw new IllegalArgumentException("A pessoa já possui associação ativa nesta família.");
        }
        var e = new MembroFamilia();
        e.setFamiliaId(fam.getId());
        e.setPessoaId(pes.getId());
        e.setTipo(x.tipo());
        e.setDataInicio(x.dataInicio());
        e.setDataFim(x.dataFim());
        return repositorio.save(e);
    }

    @Transactional(readOnly = true)
    public List<MembroFamilia> listar() {
        var os = organizacoes();
        if (os.isEmpty()) return List.of();
        return repositorio.findAll().stream()
                .filter(m -> pessoas.findById(m.getPessoaId()).map(pe -> pessoas.isVisivel(pe.getUuid(), os)).orElse(false))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MembroFamilia> listarPorFamilia(UUID familiaUuid) {
        var fam = familias.findByUuid(familiaUuid).orElseThrow(() -> new IllegalArgumentException("Família não encontrada."));
        var os = organizacoes();
        boolean admin = ehAdmin();
        return repositorio.findByFamiliaId(fam.getId()).stream()
                .filter(m -> admin || (!os.isEmpty() && pessoas.findById(m.getPessoaId()).map(pe -> pessoas.isVisivel(pe.getUuid(), os)).orElse(false)))
                .toList();
    }

    @Transactional(readOnly = true)
    public MembroFamilia buscar(UUID uuid) {
        var e = repositorio.findByUuid(uuid).orElseThrow(() -> new IllegalArgumentException("Membro de família não encontrado."));
        var pes = pessoas.findById(e.getPessoaId()).orElseThrow(() -> new IllegalArgumentException("Pessoa do membro não encontrada."));
        validarPessoa(pes.getUuid());
        return e;
    }

    @Transactional(readOnly = true)
    public List<MembroFamiliaView> listarPorFamiliaView(UUID familiaUuid) {
        var fam = familias.findByUuid(familiaUuid).orElseThrow(() -> new IllegalArgumentException("Família não encontrada."));
        var os = organizacoes();
        boolean admin = ehAdmin();
        return repositorio.findByFamiliaId(fam.getId()).stream()
                .filter(m -> admin || (!os.isEmpty() && pessoas.findById(m.getPessoaId()).map(pe -> pessoas.isVisivel(pe.getUuid(), os)).orElse(false)))
                .map(m -> toView(m, fam, pessoas.findById(m.getPessoaId()).orElse(null)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MembroFamiliaView> listarPorPessoaView(UUID pessoaUuid) {
        var pessoa = pessoas.findByUuid(pessoaUuid).orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada."));
        validarPessoa(pessoaUuid);
        return repositorio.findByPessoaId(pessoa.getId()).stream()
                .map(m -> familias.findById(m.getFamiliaId()).map(fam -> toView(m, fam, pessoa)).orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private MembroFamiliaView toView(MembroFamilia m, br.com.admeclesial.domain.pessoa.Familia fam, Pessoa pes) {
        return new MembroFamiliaView(m.getUuid(), fam.getUuid(), fam.getNome(),
                pes != null ? pes.getUuid() : null, pes != null ? pes.getNome() : null,
                m.getTipo(), m.getDataInicio(), m.getDataFim());
    }

    @Transactional
    public MembroFamilia encerrar(UUID uuid, LocalDate dataFim) {
        var e = repositorio.findByUuid(uuid).orElseThrow(() -> new IllegalArgumentException("Membro de família não encontrado."));
        var pes = pessoas.findById(e.getPessoaId()).orElseThrow(() -> new IllegalArgumentException("Pessoa do membro não encontrada."));
        validarPessoa(pes.getUuid());
        if (dataFim == null) throw new IllegalArgumentException("Data de fim é obrigatória.");
        if (dataFim.isBefore(e.getDataInicio())) throw new IllegalArgumentException("Data de fim não pode ser anterior à de início.");
        e.setDataFim(dataFim);
        return repositorio.save(e);
    }

    private void validarPessoa(UUID uuid) {
        var os = organizacoes();
        if (os.isEmpty() || !pessoas.isVisivel(uuid, os)) {
            throw new AccessDeniedException("Pessoa fora das organizações autorizadas");
        }
    }

    private Set<UUID> organizacoes() {
        return seguranca == null ? Set.of() : seguranca.organizacoesAutorizadas();
    }

    private boolean ehAdmin() {
        return seguranca != null && seguranca.ehAdministrador();
    }
}
