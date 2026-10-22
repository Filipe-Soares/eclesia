package br.com.admeclesial.infrastructure.persistence.sacramento;
import br.com.admeclesial.domain.sacramento.*; import org.springframework.data.jpa.repository.*; import java.util.*;
public interface ParticipanteSacramentoRepository extends JpaRepository<ParticipanteSacramento,Long>{ Optional<ParticipanteSacramento> findByUuid(UUID uuid); List<ParticipanteSacramento> findAllBySacramentoIdOrderByPessoaNomeAsc(Long sacramentoId); boolean existsBySacramentoIdAndPessoaIdAndTipoParticipacao(Long sacramentoId,Long pessoaId,TipoParticipacaoSacramento tipo); }
