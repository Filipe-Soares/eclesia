package br.com.admeclesial.interfaces.rest.organizacao;

import br.com.admeclesial.application.organizacao.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/funcoes-eclesiais")
public class FuncaoEclesialRestController {
    private final FuncaoEclesialApplicationService service;
    public FuncaoEclesialRestController(FuncaoEclesialApplicationService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FuncaoEclesialResponse criar(@Valid @RequestBody FuncaoEclesialRequest request) { return service.criar(request); }

    @GetMapping("/{uuid}")
    public FuncaoEclesialResponse buscar(@PathVariable UUID uuid) { return service.buscar(uuid); }

    @GetMapping
    public List<FuncaoEclesialResponse> listar() { return service.listar(); }
}
