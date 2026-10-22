package br.com.admeclesial.infrastructure.persistence.relatorio;
import br.com.admeclesial.domain.sacramento.Sacramento; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.time.LocalDate; import java.util.UUID;
public interface RelatorioSacramentoRepository extends JpaRepository<Sacramento,Long>{
 @Query(value="""
 select s.uuid,s.tipo,s.data_celebracao as dataCelebracao,
 coalesce((select string_agg(p.nome, ', ' order by p.nome) from adm_eclesial.participante_sacramento ps join adm_eclesial.pessoa p on p.id=ps.pessoa_id where ps.sacramento_id=s.id and ps.tipo_participacao in ('SACRAMENTANDO','CONJUGE')),'') as pessoaPrincipal,
 s.local,s.status,r.livro,r.folha,r.termo,r.numero_registro as numeroRegistro
 from adm_eclesial.sacramento s join adm_eclesial.organizacao_eclesial o on o.id=s.organizacao_eclesial_id left join adm_eclesial.registro_sacramental r on r.sacramento_id=s.id
 where o.uuid=:org and (:tipo is null or s.tipo=:tipo) and (s.data_celebracao>=coalesce(:inicio,s.data_celebracao)) and (s.data_celebracao<=coalesce(:fim,s.data_celebracao))
 and (:pessoa is null or exists(select 1 from adm_eclesial.participante_sacramento ps2 join adm_eclesial.pessoa p2 on p2.id=ps2.pessoa_id where ps2.sacramento_id=s.id and lower(p2.nome) like lower(concat('%',cast(:pessoa as text),'%'))))
 order by s.data_celebracao desc,s.id desc
 """,countQuery="""
 select count(*) from adm_eclesial.sacramento s join adm_eclesial.organizacao_eclesial o on o.id=s.organizacao_eclesial_id
 where o.uuid=:org and (:tipo is null or s.tipo=:tipo) and (s.data_celebracao>=coalesce(:inicio,s.data_celebracao)) and (s.data_celebracao<=coalesce(:fim,s.data_celebracao))
 and (:pessoa is null or exists(select 1 from adm_eclesial.participante_sacramento ps2 join adm_eclesial.pessoa p2 on p2.id=ps2.pessoa_id where ps2.sacramento_id=s.id and lower(p2.nome) like lower(concat('%',cast(:pessoa as text),'%'))))
 """,nativeQuery=true)
 Page<RelatorioSacramentoRegistroProjection> registros(@Param("org") UUID org,@Param("tipo") String tipo,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim,@Param("pessoa") String pessoa,Pageable pageable);
 @Query(value="""
 select p.uuid as pessoaUuid,p.nome as pessoa,s.tipo,s.data_celebracao as dataCelebracao,s.uuid as sacramentoUuid,s.status
 from adm_eclesial.participante_sacramento ps join adm_eclesial.sacramento s on s.id=ps.sacramento_id join adm_eclesial.pessoa p on p.id=ps.pessoa_id join adm_eclesial.organizacao_eclesial o on o.id=s.organizacao_eclesial_id
 where o.uuid=:org and ps.tipo_participacao in ('SACRAMENTANDO','CONJUGE') and (:tipo is null or s.tipo=:tipo) and (s.data_celebracao>=coalesce(:inicio,s.data_celebracao)) and (s.data_celebracao<=coalesce(:fim,s.data_celebracao)) and (:pessoa is null or lower(p.nome) like lower(concat('%',cast(:pessoa as text),'%')))
 order by p.nome,s.data_celebracao desc
 """,countQuery="""
 select count(*) from adm_eclesial.participante_sacramento ps join adm_eclesial.sacramento s on s.id=ps.sacramento_id join adm_eclesial.pessoa p on p.id=ps.pessoa_id join adm_eclesial.organizacao_eclesial o on o.id=s.organizacao_eclesial_id
 where o.uuid=:org and ps.tipo_participacao in ('SACRAMENTANDO','CONJUGE') and (:tipo is null or s.tipo=:tipo) and (s.data_celebracao>=coalesce(:inicio,s.data_celebracao)) and (s.data_celebracao<=coalesce(:fim,s.data_celebracao)) and (:pessoa is null or lower(p.nome) like lower(concat('%',cast(:pessoa as text),'%')))
 """,nativeQuery=true)
 Page<RelatorioSacramentoPessoaProjection> pessoas(@Param("org") UUID org,@Param("tipo") String tipo,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim,@Param("pessoa") String pessoa,Pageable pageable);
}
