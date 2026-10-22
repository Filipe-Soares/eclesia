package br.com.admeclesial.infrastructure.persistence.organizacao;

import br.com.admeclesial.domain.organizacao.VinculoFuncaoEclesial;
import br.com.admeclesial.domain.pessoa.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface VinculoFuncaoRepository extends JpaRepository<VinculoFuncaoEclesial, Long> {
    java.util.Optional<VinculoFuncaoEclesial> findByUuid(UUID uuid);
    List<VinculoFuncaoEclesial> findAllByOrderByDataInicioDesc();

    @Query("""
        select count(v) > 0 from VinculoFuncaoEclesial v
        where v.vinculoPessoaOrganizacao.organizacaoEclesial.id = :organizacaoId
          and v.funcaoEclesial.id = :funcaoId
          and v.dataInicio <= coalesce(:dataFim, v.dataInicio)
          and (v.dataFim is null or v.dataFim >= :dataInicio)
          and (:uuid is null or v.uuid <> :uuid)
    """)
    boolean existsConflitoFuncao(@Param("organizacaoId") Long organizacaoId,
                                 @Param("funcaoId") Long funcaoId,
                                 @Param("dataInicio") LocalDate dataInicio,
                                 @Param("dataFim") LocalDate dataFim,
                                 @Param("uuid") UUID uuid);

    @Query("""
        select count(v) > 0 from VinculoFuncaoEclesial v
        where v.vinculoPessoaOrganizacao.id = :vinculoId
          and v.funcaoEclesial.id = :funcaoId
          and v.dataInicio <= coalesce(:dataFim, v.dataInicio)
          and (v.dataFim is null or v.dataFim >= :dataInicio)
          and (:uuid is null or v.uuid <> :uuid)
    """)
    boolean existsConflitoFuncaoNoVinculo(@Param("vinculoId") Long vinculoId,
                                          @Param("funcaoId") Long funcaoId,
                                          @Param("dataInicio") LocalDate dataInicio,
                                          @Param("dataFim") LocalDate dataFim,
                                          @Param("uuid") UUID uuid);

    @Query("""
        select count(v) > 0 from VinculoFuncaoEclesial v
        where v.vinculoPessoaOrganizacao.id = :vinculoId
          and v.principal = true
          and v.dataInicio <= coalesce(:dataFim, v.dataInicio)
          and (v.dataFim is null or v.dataFim >= :dataInicio)
          and (:uuid is null or v.uuid <> :uuid)
    """)
    boolean existsPrincipalConflitante(@Param("vinculoId") Long vinculoId,
                                       @Param("dataInicio") LocalDate dataInicio,
                                       @Param("dataFim") LocalDate dataFim,
                                       @Param("uuid") UUID uuid);

    @Query("""
        select count(v) > 0 from VinculoFuncaoEclesial v
        where v.vinculoPessoaOrganizacao.pessoa.id = :pessoaId
          and v.vinculoPessoaOrganizacao.organizacaoEclesial.id = :organizacaoId
          and upper(v.funcaoEclesial.codigo) = 'CATEQUISTA'
          and v.funcaoEclesial.ativo = true
          and v.vinculoPessoaOrganizacao.status = br.com.admeclesial.domain.organizacao.StatusVinculoPessoaOrganizacao.ATIVO
          and v.dataInicio <= coalesce(:dataFim, v.dataInicio)
          and (v.dataFim is null or v.dataFim >= :dataInicio)
    """)
    boolean pessoaPossuiFuncaoCatequista(@Param("pessoaId") Long pessoaId,
                                         @Param("organizacaoId") Long organizacaoId,
                                         @Param("dataInicio") LocalDate dataInicio,
                                         @Param("dataFim") LocalDate dataFim);

    @Query("""
        select distinct v.vinculoPessoaOrganizacao.pessoa
        from VinculoFuncaoEclesial v
        where v.vinculoPessoaOrganizacao.organizacaoEclesial.id = :organizacaoId
          and upper(v.funcaoEclesial.codigo) = 'CATEQUISTA'
          and v.funcaoEclesial.ativo = true
          and v.vinculoPessoaOrganizacao.status = br.com.admeclesial.domain.organizacao.StatusVinculoPessoaOrganizacao.ATIVO
          and v.vinculoPessoaOrganizacao.pessoa.ativo = true
          and v.dataInicio <= coalesce(:dataFim, v.dataInicio)
          and (v.dataFim is null or v.dataFim >= coalesce(:dataInicio, v.dataFim))
        order by v.vinculoPessoaOrganizacao.pessoa.nome
    """)
    List<Pessoa> findPessoasElegiveisComoCatequista(@Param("organizacaoId") Long organizacaoId,
                                                    @Param("dataInicio") LocalDate dataInicio,
                                                    @Param("dataFim") LocalDate dataFim);
}
