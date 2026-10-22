package br.com.admeclesial.infrastructure.persistence.organizacao;

import br.com.admeclesial.domain.organizacao.VinculoPessoaOrganizacaoEclesial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VinculoPessoaOrganizacaoRepository extends JpaRepository<VinculoPessoaOrganizacaoEclesial, Long> {
    Optional<VinculoPessoaOrganizacaoEclesial> findByUuid(UUID uuid);
    List<VinculoPessoaOrganizacaoEclesial> findAllByOrderByDataInicioDesc();

    @Query("""
        select count(v) > 0 from VinculoPessoaOrganizacaoEclesial v
        where v.pessoa.id = :pessoaId
          and v.organizacaoEclesial.id = :organizacaoId
          and v.principal = true
          and v.status = br.com.admeclesial.domain.organizacao.StatusVinculoPessoaOrganizacao.ATIVO
          and (:uuid is null or v.uuid <> :uuid)
          and v.dataInicio <= coalesce(:dataFim, v.dataInicio)
          and (v.dataFim is null or v.dataFim >= :dataInicio)
    """)
    boolean existsPrincipalConflitante(@Param("pessoaId") Long pessoaId,
                                       @Param("organizacaoId") Long organizacaoId,
                                       @Param("dataInicio") java.time.LocalDate dataInicio,
                                       @Param("dataFim") java.time.LocalDate dataFim,
                                       @Param("uuid") UUID uuid);
}
