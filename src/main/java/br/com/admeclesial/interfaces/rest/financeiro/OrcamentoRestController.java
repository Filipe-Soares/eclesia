package br.com.admeclesial.interfaces.rest.financeiro;
import br.com.admeclesial.application.financeiro.*;
import br.com.admeclesial.domain.financeiro.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/orcamentos")
public class OrcamentoRestController {
 final OrcamentoApplicationService s; public OrcamentoRestController(OrcamentoApplicationService s){this.s=s;}
 @PostMapping public Orcamento criar(@Valid @RequestBody OrcamentoRequest r){return s.criar(r);}
 @GetMapping public List<Orcamento> listar(){return s.listar();}
 @GetMapping("/{uuid}") public Orcamento buscar(@PathVariable UUID uuid){return s.buscar(uuid);}
 @PostMapping("/{uuid}/itens") public ItemOrcamento adicionarItem(@PathVariable UUID uuid,@Valid @RequestBody ItemOrcamentoRequest r){return s.adicionarItem(uuid,r);}
 @GetMapping("/{uuid}/itens") public List<ItemOrcamento> listarItens(@PathVariable UUID uuid){return s.listarItens(uuid);}
 @PostMapping("/{uuid}/aprovar") public Orcamento aprovar(@PathVariable UUID uuid){return s.aprovar(uuid);}
 @PostMapping("/{uuid}/encerrar") public Orcamento encerrar(@PathVariable UUID uuid){return s.encerrar(uuid);}
 @PostMapping("/{uuid}/cancelar") public Orcamento cancelar(@PathVariable UUID uuid){return s.cancelar(uuid);}
}
