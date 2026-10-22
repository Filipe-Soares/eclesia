package br.com.admeclesial.interfaces.rest.sacramento;
import br.com.admeclesial.application.sacramento.*; import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/participantes-sacramento") public class ParticipanteSacramentoRestController {
 private final ParticipanteSacramentoApplicationService service; public ParticipanteSacramentoRestController(ParticipanteSacramentoApplicationService s){service=s;}
 @PostMapping public ResponseEntity<ParticipanteSacramentoResponse> criar(@Valid @RequestBody ParticipanteSacramentoRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(r));}
 @GetMapping("/{uuid}") public ParticipanteSacramentoResponse buscar(@PathVariable UUID uuid){return service.buscar(uuid);}
 @GetMapping("/por-sacramento/{sacramentoUuid}") public List<ParticipanteSacramentoResponse> listar(@PathVariable UUID sacramentoUuid){return service.listarPorSacramento(sacramentoUuid);}
}
