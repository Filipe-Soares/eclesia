# ADM Eclesial v16.8 - Infraestrutura de Relatórios + Pessoas

## Escopo
- Sem novas tabelas e sem migration.
- Infraestrutura comum: contexto de organizações autorizadas, filtro/paginação/ordenação e resposta paginada.
- Relatório de Pessoas em `/relatorios/pessoas`.
- Filtros: organização (obrigatória), nome e situação.
- A consulta filtra por organização no banco; não usa `findAll()` seguido de filtro em memória.
- Tamanho de página limitado entre 10 e 100; padrão 25 na interface.
- Ordenações permitidas: nome e dataCadastro.
- Segurança: valida `SegurancaOrganizacaoService` antes da consulta e rota protegida por perfil.

## Próximos relatórios
Catequese, Sacramentos, Dízimos/Contribuições, Movimentações, Contas a Pagar, Repasses e Projetos/Obras.

## Validação
Execute `mvn clean test`. O ambiente de geração do ZIP não possui Maven, portanto BUILD SUCCESS deve ser confirmado no ambiente do projeto.
