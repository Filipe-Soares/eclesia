package br.com.admeclesial.interfaces.rest.sacramento;
import br.com.admeclesial.application.sacramento.*; import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/registros-sacramentais") public class RegistroSacramentalRestController {
 private final RegistroSacramentalApplicationService service; public RegistroSacramentalRestController(RegistroSacramentalApplicationService s){service=s;}
 @PutMapping public RegistroSacramentalResponse salvar(@Valid @RequestBody RegistroSacramentalRequest r){return service.salvar(r);}
 @GetMapping("/por-sacramento/{sacramentoUuid}") public ResponseEntity<RegistroSacramentalResponse> buscarPorSacramento(@PathVariable UUID sacramentoUuid){var r=service.buscarPorSacramento(sacramentoUuid);return r==null?ResponseEntity.noContent().build():ResponseEntity.ok(r);}
 @GetMapping("/{uuid}") public RegistroSacramentalResponse buscar(@PathVariable UUID uuid){return service.buscar(uuid);}
}
