package br.com.admeclesial.interfaces.web.financeiro;

import br.com.admeclesial.application.financeiro.DizimistaApplicationService;
import br.com.admeclesial.application.financeiro.DizimistaRequest;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService;
import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import br.com.admeclesial.domain.pessoa.Pessoa;
import br.com.admeclesial.infrastructure.persistence.organizacao.OrganizacaoEclesialRepository;
import br.com.admeclesial.infrastructure.persistence.pessoa.PessoaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/financeiro/dizimistas")
public class DizimistaWebController {

    private final DizimistaApplicationService service;
    private final OrganizacaoEclesialRepository orgRepo;
    private final PessoaRepository pessoaRepo;
    private final SegurancaOrganizacaoService seguranca;

    public DizimistaWebController(DizimistaApplicationService service,
                                  OrganizacaoEclesialRepository orgRepo,
                                  PessoaRepository pessoaRepo,
                                  SegurancaOrganizacaoService seguranca) {
        this.service = service;
        this.orgRepo = orgRepo;
        this.pessoaRepo = pessoaRepo;
        this.seguranca = seguranca;
    }

    @GetMapping
    public String lista(Model m) {
        m.addAttribute("dizimistas", service.listar());
        m.addAttribute("pessoasPorId", pessoaRepo.findAllByOrderByNomeAsc().stream()
                .collect(Collectors.toMap(Pessoa::getId, Pessoa::getNome)));
        m.addAttribute("orgsPorId", orgRepo.findAllByOrderByNomeAsc().stream()
                .collect(Collectors.toMap(OrganizacaoEclesial::getId, OrganizacaoEclesial::getNome)));
        return "financeiro/dizimistas/lista";
    }

    @GetMapping("/novo")
    public String novo(Model m) {
        if (!m.containsAttribute("request")) {
            m.addAttribute("request", new DizimistaRequest(null, null, null, null, true, null));
        }
        preencher(m);
        return "financeiro/dizimistas/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("request") DizimistaRequest request,
                        BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            preencher(m);
            return "financeiro/dizimistas/form";
        }
        try {
            service.salvar(null, request);
            ra.addFlashAttribute("successMessage", "Dizimista cadastrado com sucesso.");
            return "redirect:/financeiro/dizimistas";
        } catch (RuntimeException ex) {
            m.addAttribute("businessError", ex.getMessage());
            preencher(m);
            return "financeiro/dizimistas/form";
        }
    }

    @GetMapping("/{uuid}/editar")
    public String editar(@PathVariable UUID uuid, Model m) {
        var d = service.listar().stream().filter(x -> x.getUuid().equals(uuid)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Dizimista não encontrado."));
        var pessoaUuid = pessoaRepo.findById(d.getPessoaId()).map(p -> p.getUuid()).orElse(null);
        var orgUuid = orgRepo.findById(d.getOrganizacaoEclesialId()).map(o -> o.getUuid()).orElse(null);
        m.addAttribute("request", new DizimistaRequest(pessoaUuid, orgUuid, d.getDataInicio(),
                d.getDataFim(), d.getAtivo(), d.getObservacao()));
        m.addAttribute("dizimistaUuid", uuid);
        preencher(m);
        return "financeiro/dizimistas/form";
    }

    @PostMapping("/{uuid}/editar")
    public String atualizar(@PathVariable UUID uuid,
                            @Valid @ModelAttribute("request") DizimistaRequest request,
                            BindingResult bindingResult, Model m, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            m.addAttribute("dizimistaUuid", uuid);
            preencher(m);
            return "financeiro/dizimistas/form";
        }
        try {
            service.salvar(uuid, request);
            ra.addFlashAttribute("successMessage", "Dizimista atualizado com sucesso.");
            return "redirect:/financeiro/dizimistas";
        } catch (RuntimeException ex) {
            m.addAttribute("dizimistaUuid", uuid);
            m.addAttribute("businessError", ex.getMessage());
            preencher(m);
            return "financeiro/dizimistas/form";
        }
    }

    private void preencher(Model m) {
        var autorizadas = seguranca.organizacoesAutorizadas();
        m.addAttribute("organizacoes", orgRepo.findAllByOrderByNomeAsc().stream()
                .filter(o -> autorizadas.contains(o.getUuid())).toList());
        m.addAttribute("pessoas", pessoaRepo.findAllByOrderByNomeAsc());
    }
}
