package br.com.admeclesial.interfaces.rest.organizacao;

import br.com.admeclesial.application.organizacao.*;
import br.com.admeclesial.domain.organizacao.TipoOrganizacaoEclesial;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizacoes-eclesiais")
public class OrganizacaoEclesialRestController {
    private final OrganizacaoEclesialApplicationService service;
    public OrganizacaoEclesialRestController(OrganizacaoEclesialApplicationService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizacaoResponse criar(@Valid @RequestBody OrganizacaoRequest request) { return service.criar(request); }

    @GetMapping("/{uuid}")
    public OrganizacaoResponse buscar(@PathVariable UUID uuid) { return service.buscar(uuid); }

    @GetMapping
    public List<OrganizacaoResponse> listar() { return service.listar(); }

    @GetMapping("/tipos")
    public TipoOrganizacaoEclesial[] tipos() { return TipoOrganizacaoEclesial.values(); }
}
