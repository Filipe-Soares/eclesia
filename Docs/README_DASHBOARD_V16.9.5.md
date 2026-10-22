# ADM Eclesial — v16.9.5 — Dashboard CONSULTA

## Escopo

Dashboard enxuto e somente leitura para o perfil `CONSULTA`.

Indicadores:
- Pessoas ativas;
- Turmas ativas;
- Inscritos ativos;
- Sacramentos registrados no período;
- Projetos / Obras em execução.

Acessos rápidos:
- Relatório de Pessoas;
- Relatório de Catequese / Turmas;
- Relatório de Sacramentos;
- Relatório de Projetos / Obras.

## Segurança

- `/dashboard` passa a aceitar `CONSULTA`.
- O perfil recebe `DashboardConsultaResponse`, sem campos financeiros.
- Nenhuma consulta financeira é executada para montar esta visão.
- Não são exibidos contribuições, movimentações, contas a pagar, repasses, orçamento ou valores de recursos de projetos.

## Banco de dados

Nenhuma migration. Nenhuma tabela nova. Todos os indicadores são derivados do modelo existente.
