package br.com.admeclesial.interfaces.rest.sacramento;
import br.com.admeclesial.application.sacramento.*; import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/sacramentos") public class SacramentoRestController {
 private final SacramentoApplicationService service; public SacramentoRestController(SacramentoApplicationService s){service=s;}
 @PostMapping public ResponseEntity<SacramentoResponse> criar(@Valid @RequestBody SacramentoRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(r));}
 @PutMapping("/{uuid}") public SacramentoResponse atualizar(@PathVariable UUID uuid,@Valid @RequestBody SacramentoRequest r){return service.atualizar(uuid,r);}
 @GetMapping public List<SacramentoResponse> listar(){return service.listar();}
 @GetMapping("/{uuid}") public SacramentoResponse buscar(@PathVariable UUID uuid){return service.buscar(uuid);}
 @GetMapping("/por-organizacao/{organizacaoUuid}") public List<SacramentoResponse> listarPorOrganizacao(@PathVariable UUID organizacaoUuid){return service.listarPorOrganizacao(organizacaoUuid);}
}
