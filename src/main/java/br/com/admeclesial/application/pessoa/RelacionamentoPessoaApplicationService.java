package br.com.admeclesial.application.pessoa;

import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.pessoa.*;
import br.com.admeclesial.infrastructure.persistence.pessoa.PessoaRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.RelacionamentoPessoaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class RelacionamentoPessoaApplicationService {

    private final RelacionamentoPessoaRepository repositorio;
    private final PessoaRepository pessoas;
    private final SegurancaOrganizacaoService seguranca;

    public RelacionamentoPessoaApplicationService(RelacionamentoPessoaRepository repositorio,
                                                  PessoaRepository pessoas,
                                                  SegurancaOrganizacaoService seguranca) {
        this.repositorio = repositorio;
        this.pessoas = pessoas;
        this.seguranca = seguranca;
    }

    @Transactional
    public RelacionamentoPessoa criar(RelacionamentoPessoaRequest x) {
        var a = pessoas.findByUuid(x.pessoaOrigemUuid()).orElseThrow(() -> new IllegalArgumentException("Pessoa de origem não encontrada."));
        var b = pessoas.findByUuid(x.pessoaDestinoUuid()).orElseThrow(() -> new IllegalArgumentException("Pessoa de destino não encontrada."));
        validarPessoa(x.pessoaOrigemUuid());
        validarPessoa(x.pessoaDestinoUuid());
        validar(a, b, x.dataInicio(), x.dataFim());
        var e = new RelacionamentoPessoa();
        e.setPessoaOrigemId(a.getId());
        e.setPessoaDestinoId(b.getId());
        e.setTipoRelacionamento(x.tipoRelacionamento());
        e.setDataInicio(x.dataInicio());
        e.setDataFim(x.dataFim());
        e.setObservacao(x.observacao());
        var salvo = repositorio.save(e);
        criarParInverso(a, b, x.tipoRelacionamento(), x.dataInicio(), x.dataFim(), x.observacao());
        return salvo;
    }

    @Transactional(readOnly = true)
    public List<RelacionamentoPessoa> listar() {
        var os = organizacoes();
        if (os.isEmpty()) return List.of();
        return repositorio.findAll().stream().filter(r -> visivel(r, os)).toList();
    }

    @Transactional(readOnly = true)
    public List<RelacionamentoPessoa> porPessoa(UUID pessoaUuid) {
        var pessoa = pessoas.findByUuid(pessoaUuid).orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada."));
        validarPessoa(pessoaUuid);
        var os = organizacoes();
        boolean admin = ehAdmin();
        return repositorio.findByPessoaOrigemIdOrPessoaDestinoId(pessoa.getId(), pessoa.getId()).stream()
                .filter(r -> admin || (!os.isEmpty() && visivel(r, os)))
                .toList();
    }

    @Transactional(readOnly = true)
    public RelacionamentoPessoa buscar(UUID uuid) {
        var e = repositorio.findByUuid(uuid).orElseThrow(() -> new IllegalArgumentException("Relacionamento não encontrado."));
        validarVisibilidade(e);
        return e;
    }

    @Transactional(readOnly = true)
    public List<RelacionamentoPessoaView> listarPorPessoaView(UUID pessoaUuid) {
        var pessoa = pessoas.findByUuid(pessoaUuid).orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada."));
        validarPessoa(pessoaUuid);
        return repositorio.findByPessoaOrigemIdOrPessoaDestinoId(pessoa.getId(), pessoa.getId()).stream().map(r -> {
            Long outroId = r.getPessoaOrigemId().equals(pessoa.getId()) ? r.getPessoaDestinoId() : r.getPessoaOrigemId();
            var outro = pessoas.findById(outroId).orElse(null);
            return new RelacionamentoPessoaView(r.getUuid(), outro != null ? outro.getUuid() : null,
                    outro != null ? outro.getNome() : null, r.getTipoRelacionamento(), r.getDataInicio(), r.getDataFim());
        }).toList();
    }

    @Transactional
    public RelacionamentoPessoa atualizar(UUID uuid, RelacionamentoPessoaRequest x) {
        var e = repositorio.findByUuid(uuid).orElseThrow(() -> new IllegalArgumentException("Relacionamento não encontrado."));
        validarVisibilidade(e);
        var aAntigo = pessoas.findById(e.getPessoaOrigemId()).orElse(null);
        var bAntigo = pessoas.findById(e.getPessoaDestinoId()).orElse(null);
        if (aAntigo != null && bAntigo != null) {
            removerParInverso(aAntigo, bAntigo, e.getTipoRelacionamento());
        }
        var a = pessoas.findByUuid(x.pessoaOrigemUuid()).orElseThrow(() -> new IllegalArgumentException("Pessoa de origem não encontrada."));
        var b = pessoas.findByUuid(x.pessoaDestinoUuid()).orElseThrow(() -> new IllegalArgumentException("Pessoa de destino não encontrada."));
        validarPessoa(x.pessoaOrigemUuid());
        validarPessoa(x.pessoaDestinoUuid());
        validar(a, b, x.dataInicio(), x.dataFim());
        e.setPessoaOrigemId(a.getId());
        e.setPessoaDestinoId(b.getId());
        e.setTipoRelacionamento(x.tipoRelacionamento());
        e.setDataInicio(x.dataInicio());
        e.setDataFim(x.dataFim());
        e.setObservacao(x.observacao());
        var salvo = repositorio.save(e);
        criarParInverso(a, b, x.tipoRelacionamento(), x.dataInicio(), x.dataFim(), x.observacao());
        return salvo;
    }

    @Transactional
    public void remover(UUID uuid) {
        var e = repositorio.findByUuid(uuid).orElseThrow(() -> new IllegalArgumentException("Relacionamento não encontrado."));
        validarVisibilidade(e);
        var a = pessoas.findById(e.getPessoaOrigemId()).orElse(null);
        var b = pessoas.findById(e.getPessoaDestinoId()).orElse(null);
        if (a != null && b != null) {
            removerParInverso(a, b, e.getTipoRelacionamento());
        }
        repositorio.delete(e);
    }

    private void validar(Pessoa a, Pessoa b, LocalDate dataInicio, LocalDate dataFim) {
        if (a.getId().equals(b.getId())) throw new IllegalArgumentException("As pessoas devem ser diferentes.");
        if (!a.isAtivo() || !b.isAtivo()) throw new IllegalArgumentException("As pessoas devem estar ativas.");
        if (dataInicio == null) throw new IllegalArgumentException("Data de início é obrigatória.");
        if (dataFim != null && dataFim.isBefore(dataInicio)) throw new IllegalArgumentException("Período inválido.");
    }

    private void criarParInverso(Pessoa origem, Pessoa destino, TipoRelacionamentoPessoa tipo,
                                 LocalDate dataInicio, LocalDate dataFim, String observacao) {
        var inv = inverso(tipo, destino);
        boolean jaExiste = repositorio.findByPessoaOrigemIdAndPessoaDestinoId(destino.getId(), origem.getId()).stream()
                .anyMatch(r -> r.getTipoRelacionamento() == inv);
        if (jaExiste) return;
        var e = new RelacionamentoPessoa();
        e.setPessoaOrigemId(destino.getId());
        e.setPessoaDestinoId(origem.getId());
        e.setTipoRelacionamento(inv);
        e.setDataInicio(dataInicio);
        e.setDataFim(dataFim);
        e.setObservacao(observacao);
        repositorio.save(e);
    }

    private void removerParInverso(Pessoa origem, Pessoa destino, TipoRelacionamentoPessoa tipo) {
        var inv = inverso(tipo, destino);
        repositorio.findByPessoaOrigemIdAndPessoaDestinoId(destino.getId(), origem.getId()).stream()
                .filter(r -> r.getTipoRelacionamento() == inv)
                .forEach(repositorio::delete);
    }

    private TipoRelacionamentoPessoa inverso(TipoRelacionamentoPessoa tipo, Pessoa destino) {
        boolean fem = destino.getSexo() == TipoSexo.FEMININO;
        return switch (tipo) {
            case PAI, MAE -> TipoRelacionamentoPessoa.FILHO;
            case FILHO, FILHA -> fem ? TipoRelacionamentoPessoa.MAE : TipoRelacionamentoPessoa.PAI;
            case CONJUGE -> TipoRelacionamentoPessoa.CONJUGE;
            case IRMAO, IRMA -> fem ? TipoRelacionamentoPessoa.IRMA : TipoRelacionamentoPessoa.IRMAO;
            case AVO, AVA -> fem ? TipoRelacionamentoPessoa.NETA : TipoRelacionamentoPessoa.NETO;
            case NETO, NETA -> fem ? TipoRelacionamentoPessoa.AVA : TipoRelacionamentoPessoa.AVO;
            case OUTRO -> TipoRelacionamentoPessoa.OUTRO;
        };
    }

    private void validarVisibilidade(RelacionamentoPessoa e) {
        var a = pessoas.findById(e.getPessoaOrigemId()).orElse(null);
        var b = pessoas.findById(e.getPessoaDestinoId()).orElse(null);
        if (a == null || b == null) throw new IllegalArgumentException("Pessoas do relacionamento não encontradas.");
        validarPessoa(a.getUuid());
        validarPessoa(b.getUuid());
    }

    private boolean visivel(RelacionamentoPessoa r, Set<UUID> os) {
        return pessoas.findById(r.getPessoaOrigemId()).map(pe -> pessoas.isVisivel(pe.getUuid(), os)).orElse(false)
                && pessoas.findById(r.getPessoaDestinoId()).map(pe -> pessoas.isVisivel(pe.getUuid(), os)).orElse(false);
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
