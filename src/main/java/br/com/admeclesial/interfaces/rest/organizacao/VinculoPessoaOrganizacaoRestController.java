package br.com.admeclesial.interfaces.rest.organizacao;

import br.com.admeclesial.application.organizacao.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vinculos-pessoa-organizacao")
public class VinculoPessoaOrganizacaoRestController {
    private final VinculoPessoaOrganizacaoApplicationService service;
    public VinculoPessoaOrganizacaoRestController(VinculoPessoaOrganizacaoApplicationService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VinculoPessoaOrganizacaoResponse criar(@Valid @RequestBody VinculoPessoaOrganizacaoRequest request) { return service.criar(request); }

    @GetMapping("/{uuid}")
    public VinculoPessoaOrganizacaoResponse buscar(@PathVariable UUID uuid) { return service.buscar(uuid); }

    @GetMapping
    public List<VinculoPessoaOrganizacaoResponse> listar() { return service.listar(); }
}
