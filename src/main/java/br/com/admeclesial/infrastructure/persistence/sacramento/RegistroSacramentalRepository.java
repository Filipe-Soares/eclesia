package br.com.admeclesial.infrastructure.persistence.sacramento;
import br.com.admeclesial.domain.sacramento.RegistroSacramental; import org.springframework.data.jpa.repository.*; import java.util.*;
public interface RegistroSacramentalRepository extends JpaRepository<RegistroSacramental,Long>{ Optional<RegistroSacramental> findByUuid(UUID uuid); Optional<RegistroSacramental> findBySacramentoId(Long sacramentoId); }
