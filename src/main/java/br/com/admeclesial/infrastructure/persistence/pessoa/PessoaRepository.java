package br.com.admeclesial.infrastructure.persistence.pessoa;

import br.com.admeclesial.domain.pessoa.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    Optional<Pessoa> findByUuid(UUID uuid);
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndUuidNot(String cpf, UUID uuid);
    List<Pessoa> findAllByOrderByNomeAsc();

    @Query("""
        select distinct p from Pessoa p
        where not exists (select 1 from br.com.admeclesial.domain.organizacao.VinculoPessoaOrganizacaoEclesial v where v.pessoa = p)
           or exists (select 1 from br.com.admeclesial.domain.organizacao.VinculoPessoaOrganizacaoEclesial v where v.pessoa = p and v.organizacaoEclesial.uuid in :organizacoes)
        order by p.nome
    """)
    List<Pessoa> findVisiveis(@Param("organizacoes") java.util.Set<UUID> organizacoes);

    @Query("""
        select (count(p) > 0) from Pessoa p
        where p.uuid = :pessoaUuid
          and (
              not exists (select 1 from br.com.admeclesial.domain.organizacao.VinculoPessoaOrganizacaoEclesial v where v.pessoa = p)
              or exists (select 1 from br.com.admeclesial.domain.organizacao.VinculoPessoaOrganizacaoEclesial v where v.pessoa = p and v.organizacaoEclesial.uuid in :organizacoes)
          )
    """)
    boolean isVisivel(@Param("pessoaUuid") UUID pessoaUuid, @Param("organizacoes") java.util.Set<UUID> organizacoes);

    @Query("""
        select distinct p from VinculoPessoaOrganizacaoEclesial v join v.pessoa p
        where v.organizacaoEclesial.uuid = :organizacaoUuid
          and (:nome is null or lower(p.nome) like lower(concat('%', :nome, '%'))
               or lower(coalesce(p.nomeSocial, '')) like lower(concat('%', :nome, '%')))
          and (:ativo is null or p.ativo = :ativo)
    """)
    Page<Pessoa> pesquisarRelatorio(@Param("organizacaoUuid") UUID organizacaoUuid, @Param("nome") String nome, @Param("ativo") Boolean ativo, Pageable pageable);
}
