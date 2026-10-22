package br.com.admeclesial.infrastructure.persistence.organizacao;

import br.com.admeclesial.domain.organizacao.OrganizacaoEclesial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizacaoEclesialRepository extends JpaRepository<OrganizacaoEclesial, Long> {
    Optional<OrganizacaoEclesial> findByUuid(UUID uuid);
    List<OrganizacaoEclesial> findAllByOrderByNomeAsc();
}
