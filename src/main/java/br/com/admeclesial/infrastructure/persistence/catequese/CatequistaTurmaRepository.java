package br.com.admeclesial.infrastructure.persistence.catequese;

import br.com.admeclesial.domain.catequese.CatequistaTurma;
import org.springframework.data.jpa.repository.*;
import java.time.LocalDate;
import java.util.*;

public interface CatequistaTurmaRepository extends JpaRepository<CatequistaTurma, Long> {
    Optional<CatequistaTurma> findByUuid(UUID uuid);
    List<CatequistaTurma> findAllByTurmaCatequeseIdOrderByPrincipalDescDataInicioAsc(Long turmaId);

    @Query("""
        select count(c) > 0 from CatequistaTurma c
        where c.turmaCatequese.id = :turmaId
          and c.principal = true
          and (c.dataFim is null or c.dataFim >= :inicio)
          and c.dataInicio <= coalesce(:fim, c.dataInicio)
          and c.id <> coalesce(:ignorarId, -1L)
    """)
    boolean existsPrincipalConflitante(Long turmaId, LocalDate inicio, LocalDate fim, Long ignorarId);

    @Query("""
        select count(c) > 0 from CatequistaTurma c
        where c.turmaCatequese.id = :turmaId
          and c.pessoa.id = :pessoaId
          and (c.dataFim is null or c.dataFim >= :inicio)
          and c.dataInicio <= coalesce(:fim, c.dataInicio)
          and c.id <> coalesce(:ignorarId, -1L)
    """)
    boolean existsPessoaConflitante(Long turmaId, Long pessoaId, LocalDate inicio, LocalDate fim, Long ignorarId);
}
