package br.com.admeclesial.interfaces.rest.pessoa;

import br.com.admeclesial.application.pessoa.PessoaApplicationService;
import br.com.admeclesial.application.pessoa.PessoaRequest;
import br.com.admeclesial.application.pessoa.PessoaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pessoas")
public class PessoaRestController {
    private final PessoaApplicationService service;
    public PessoaRestController(PessoaApplicationService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PessoaResponse criar(@Valid @RequestBody PessoaRequest request) { return service.criar(request); }

    @GetMapping("/{uuid}")
    public PessoaResponse buscar(@PathVariable UUID uuid) { return service.buscar(uuid); }

    @GetMapping
    public List<PessoaResponse> listar() { return service.listar(); }
}
