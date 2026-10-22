package br.com.admeclesial.interfaces.web.projeto;
import br.com.admeclesial.application.projeto.*;
import br.com.admeclesial.application.financeiro.*;
import br.com.admeclesial.domain.projeto.TipoProjeto;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import br.com.admeclesial.infrastructure.persistence.financeiro.ItemOrcamentoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Controller
@RequestMapping("/projetos")
public class ProjetoWebController {
    final ProjetoApplicationService projetos;
    final RecursoVinculadoApplicationService recursos;
    final DestinacaoContribuicaoApplicationService destinos;
    final OrganizacaoEclesialRepository orgRepo;
    final ItemOrcamentoRepository itemRepo;

    public ProjetoWebController(ProjetoApplicationService p,
                                RecursoVinculadoApplicationService r,
                                DestinacaoContribuicaoApplicationService d,
                                OrganizacaoEclesialRepository orgRepo,
                                ItemOrcamentoRepository itemRepo) {
        this.projetos = p;
        this.recursos = r;
        this.destinos = d;
        this.orgRepo = orgRepo;
        this.itemRepo = itemRepo;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("projetos", projetos.listar());
        return "projeto/lista";
    }

    @GetMapping("/novo")
    public String novo(Model m) {
        m.addAttribute("tipos", TipoProjeto.values());
        m.addAttribute("projeto", new ProjetoForm());
        return "projeto/form";
    }

    @PostMapping
    public String criar(@ModelAttribute ProjetoForm f) {
        var salvo = projetos.criar(f.toRequest());
        return "redirect:/projetos/" + salvo.getUuid();
    }

    @GetMapping("/{uuid}/editar")
    public String editar(@PathVariable UUID uuid, Model m) {
        var p = projetos.buscar(uuid);
        var f = new ProjetoForm();
        if (p.getOrganizacaoEclesialId() != null) {
            orgRepo.findById(p.getOrganizacaoEclesialId()).ifPresent(o -> f.setOrganizacaoEclesialUuid(o.getUuid()));
        }
        if (p.getItemOrcamentoId() != null) {
            itemRepo.findById(p.getItemOrcamentoId()).ifPresent(i -> f.setItemOrcamentoUuid(i.getUuid()));
        }
        f.setTipo(p.getTipo());
        f.setNome(p.getNome());
        f.setDescricao(p.getDescricao());
        f.setDataInicio(p.getDataInicio());
        f.setDataFim(p.getDataFim());
        f.setValorEstimado(p.getValorEstimado());
        m.addAttribute("projetoUuid", uuid);
        m.addAttribute("tipos", TipoProjeto.values());
        m.addAttribute("projeto", f);
        return "projeto/form";
    }

    @PostMapping("/{uuid}/editar")
    public String atualizar(@PathVariable UUID uuid, @ModelAttribute ProjetoForm f) {
        projetos.atualizar(uuid, f.toRequest());
        return "redirect:/projetos/" + uuid;
    }

    @GetMapping("/{uuid}")
    public String detalhe(@PathVariable UUID uuid, Model m) {
        m.addAttribute("projeto", projetos.buscar(uuid));
        m.addAttribute("fontes", projetos.listarFontes(uuid));
        return "projeto/detalhe";
    }

    @GetMapping("/recursos")
    public String recursos(Model m) {
        m.addAttribute("recursos", recursos.listar());
        m.addAttribute("destinacoes", destinos.listar());
        return "projeto/recursos";
    }

    public static class ProjetoForm {
        public UUID organizacaoEclesialUuid;
        public UUID itemOrcamentoUuid;
        public TipoProjeto tipo;
        public String nome;
        public String descricao;
        public java.time.LocalDate dataInicio;
        public java.time.LocalDate dataFim;
        public java.math.BigDecimal valorEstimado;

        public ProjetoRequest toRequest() {
            return new ProjetoRequest(organizacaoEclesialUuid, itemOrcamentoUuid, tipo, nome, descricao, dataInicio, dataFim, valorEstimado);
        }

        public UUID getOrganizacaoEclesialUuid() { return organizacaoEclesialUuid; }
        public void setOrganizacaoEclesialUuid(UUID v) { organizacaoEclesialUuid = v; }
        public UUID getItemOrcamentoUuid() { return itemOrcamentoUuid; }
        public void setItemOrcamentoUuid(UUID v) { itemOrcamentoUuid = v; }
        public TipoProjeto getTipo() { return tipo; }
        public void setTipo(TipoProjeto v) { tipo = v; }
        public String getNome() { return nome; }
        public void setNome(String v) { nome = v; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String v) { descricao = v; }
        public java.time.LocalDate getDataInicio() { return dataInicio; }
        public void setDataInicio(java.time.LocalDate v) { dataInicio = v; }
        public java.time.LocalDate getDataFim() { return dataFim; }
        public void setDataFim(java.time.LocalDate v) { dataFim = v; }
        public java.math.BigDecimal getValorEstimado() { return valorEstimado; }
        public void setValorEstimado(java.math.BigDecimal v) { valorEstimado = v; }
    }
}
