package br.com.admeclesial.interfaces.web.matrimonio;

import br.com.admeclesial.application.matrimonio.*;
import br.com.admeclesial.application.organizacao.OrganizacaoEclesialApplicationService;
import br.com.admeclesial.application.pessoa.PessoaApplicationService;
import br.com.admeclesial.domain.matrimonio.StatusProcessoMatrimonial;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/processos-matrimoniais")
public class ProcessoMatrimonialWebController {
    final ProcessoMatrimonialApplicationService s;
    final OrganizacaoEclesialApplicationService org;
    final PessoaApplicationService pessoa;

    public ProcessoMatrimonialWebController(ProcessoMatrimonialApplicationService s,
                                            OrganizacaoEclesialApplicationService o,
                                            PessoaApplicationService p) {
        this.s = s;
        this.org = o;
        this.pessoa = p;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("processos", s.listar());
        return "matrimonio/processo-lista";
    }

    @GetMapping("/novo")
    public String novo(Model m) {
        m.addAttribute("organizacoes", org.listar());
        m.addAttribute("pessoas", pessoa.listar());
        return "matrimonio/processo-form";
    }

    @PostMapping
    public String criar(@RequestParam UUID organizacaoEclesialUuid,
                        @RequestParam UUID nubente1Uuid,
                        @RequestParam UUID nubente2Uuid,
                        @RequestParam LocalDate dataAbertura,
                        @RequestParam(required = false) LocalDate dataEncerramento,
                        @RequestParam(required = false) String observacao) {
        var resp = s.criar(new ProcessoMatrimonialRequest(organizacaoEclesialUuid, nubente1Uuid, nubente2Uuid, dataAbertura, dataEncerramento, null, observacao));
        return "redirect:/processos-matrimoniais/" + resp.uuid();
    }

    @GetMapping("/{uuid}/editar")
    public String editar(@PathVariable UUID uuid, Model m) {
        var proc = s.buscar(uuid);
        m.addAttribute("processoUuid", uuid);
        m.addAttribute("processo", proc);
        m.addAttribute("organizacoes", org.listar());
        m.addAttribute("pessoas", pessoa.listar());
        m.addAttribute("statusList", StatusProcessoMatrimonial.values());
        return "matrimonio/processo-form";
    }

    @PostMapping("/{uuid}/editar")
    public String atualizar(@PathVariable UUID uuid,
                            @RequestParam UUID organizacaoEclesialUuid,
                            @RequestParam UUID nubente1Uuid,
                            @RequestParam UUID nubente2Uuid,
                            @RequestParam LocalDate dataAbertura,
                            @RequestParam(required = false) LocalDate dataEncerramento,
                            @RequestParam(required = false) StatusProcessoMatrimonial status,
                            @RequestParam(required = false) String observacao) {
        s.atualizar(uuid, new ProcessoMatrimonialRequest(organizacaoEclesialUuid, nubente1Uuid, nubente2Uuid, dataAbertura, dataEncerramento, status, observacao));
        return "redirect:/processos-matrimoniais/" + uuid;
    }

    @GetMapping("/{uuid}")
    public String detalhe(@PathVariable UUID uuid, Model m) {
        m.addAttribute("processo", s.buscar(uuid));
        return "matrimonio/processo-detalhe";
    }
}