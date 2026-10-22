package br.com.admeclesial.interfaces.rest.organizacao;

import br.com.admeclesial.application.organizacao.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vinculos-funcoes-eclesiais")
public class VinculoFuncaoRestController {
    private final VinculoFuncaoApplicationService service;
    public VinculoFuncaoRestController(VinculoFuncaoApplicationService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VinculoFuncaoResponse criar(@Valid @RequestBody VinculoFuncaoRequest request) { return service.criar(request); }

    @GetMapping("/{uuid}")
    public VinculoFuncaoResponse buscar(@PathVariable UUID uuid) { return service.buscar(uuid); }

    @GetMapping
    public List<VinculoFuncaoResponse> listar() { return service.listar(); }
}
