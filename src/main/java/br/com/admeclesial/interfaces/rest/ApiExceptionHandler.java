package br.com.admeclesial.interfaces.rest;

import br.com.admeclesial.application.pessoa.PessoaBusinessException;
import br.com.admeclesial.application.pessoa.PessoaNotFoundException;
import br.com.admeclesial.application.organizacao.OrganizacaoNotFoundException;
import br.com.admeclesial.application.organizacao.FuncaoEclesialNotFoundException;
import br.com.admeclesial.application.organizacao.VinculoNotFoundException;
import br.com.admeclesial.application.organizacao.VinculoBusinessException;
import br.com.admeclesial.application.catequese.CatequeseBusinessException;
import br.com.admeclesial.application.catequese.CatequeseNotFoundException;
import br.com.admeclesial.application.sacramento.SacramentoBusinessException;
import br.com.admeclesial.application.sacramento.SacramentoNotFoundException;
import br.com.admeclesial.application.sacramento.ParticipanteSacramentoNotFoundException;
import br.com.admeclesial.application.sacramento.RegistroSacramentalNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "br.com.admeclesial.interfaces.rest")
public class ApiExceptionHandler {

    @ExceptionHandler({PessoaNotFoundException.class, OrganizacaoNotFoundException.class, FuncaoEclesialNotFoundException.class, VinculoNotFoundException.class, CatequeseNotFoundException.class, SacramentoNotFoundException.class, ParticipanteSacramentoNotFoundException.class, RegistroSacramentalNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(RuntimeException ex, HttpServletRequest request) {
        return error(404, "RECURSO_NAO_ENCONTRADO", ex.getMessage(), request);
    }

    @ExceptionHandler({PessoaBusinessException.class, VinculoBusinessException.class, CatequeseBusinessException.class, SacramentoBusinessException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiError business(RuntimeException ex, HttpServletRequest request) {
        return error(422, "REGRA_NEGOCIO", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiError illegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return error(422, "REGRA_NEGOCIO", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return error(400, "VALIDACAO", message, request);
    }

    private ApiError error(int status, String code, String message, HttpServletRequest request) {
        return new ApiError(OffsetDateTime.now(), status, code, message, request.getRequestURI());
    }

    public record ApiError(
            OffsetDateTime timestamp,
            int status,
            String code,
            String message,
            String path
    ) {
    }
}
