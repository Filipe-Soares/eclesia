# v16.8.7 — Relatório Projetos / Obras

Último relatório do escopo v16.8. Rota: `/relatorios/projetos-obras`.

- Ordenação: EM_EXECUCAO, PLANEJADO, CONCLUIDO, CANCELADO; dentro do status, data de início mais recente.
- Filtros: organização, tipo, situação, período e filtro rápido de recurso vinculado sem utilização.
- Indicador derivado: recursos vinculados > 0 e recursos utilizados = 0.
- ADMINISTRADOR/FINANCEIRO: visão financeira completa.
- CONSULTA: projetos sem valores financeiros; a supressão ocorre no application service e no template.
- Totais derivados de FONTE_RECURSO_PROJETO e UTILIZACAO_RECURSO.
- Nenhuma migration e nenhum total financeiro persistido.

Validação final: `mvn clean test`.
