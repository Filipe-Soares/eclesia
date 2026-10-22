# ADM Eclesial v16.6 — Projeto/Obra + Recursos Vinculados

## Escopo

A v16.6 adiciona duas etapas do ciclo financeiro:

1. Cadastro e ciclo de vida de `PROJETO`, sendo `OBRA` um tipo de projeto.
2. Fluxo de `DESTINACAO_CONTRIBUICAO` → `RECURSO_VINCULADO` → `FONTE_RECURSO_PROJETO` → `UTILIZACAO_RECURSO` → `MOVIMENTACAO_FINANCEIRA`.

## Projeto

`PROJETO` pertence a uma `ORGANIZACAO_ECLESIAL` e pode ser ligado opcionalmente a um `ITEM_ORCAMENTO`.

Tipos: `PROJETO`, `OBRA`, `PASTORAL`, `SOCIAL`, `OUTRO`.

Status: `PLANEJADO`, `EM_EXECUCAO`, `CONCLUIDO`, `CANCELADO`.

Regras principais:
- organização deve existir e estar ativa;
- datas devem formar período válido;
- item de orçamento, quando informado, deve pertencer à mesma organização;
- item de orçamento cancelado não pode ser vinculado;
- fluxo de status: PLANEJADO → EM_EXECUCAO → CONCLUIDO; cancelamento antes da conclusão;
- não há valor realizado armazenado no projeto.

## Recursos vinculados

`RECURSO_VINCULADO` nasce de uma `DESTINACAO_CONTRIBUICAO` com `FINALIDADE_ESPECIFICA`. O valor original é o valor da destinação e não é duplicado como saldo.

Status: `DISPONIVEL`, `PARCIALMENTE_UTILIZADO`, `UTILIZADO`, `CANCELADO`.

`FONTE_RECURSO_PROJETO` permite distribuir um recurso entre projetos. A soma das alocações não pode superar o valor original do recurso.

`UTILIZACAO_RECURSO` vincula uma parcela do valor alocado a uma `MOVIMENTACAO_FINANCEIRA` do tipo `SAIDA`, permitindo rastrear o custo real do projeto sem duplicar o lançamento financeiro.

## REST

### Projetos
- `POST /api/v1/projetos`
- `GET /api/v1/projetos`
- `GET /api/v1/projetos/{uuid}`
- `POST /api/v1/projetos/{uuid}/iniciar`
- `POST /api/v1/projetos/{uuid}/concluir`
- `POST /api/v1/projetos/{uuid}/cancelar`
- `GET /api/v1/projetos/{uuid}/fontes`
- `POST /api/v1/projetos/{uuid}/fontes`

### Destinações
- `POST /api/v1/destinacoes-contribuicao`
- `GET /api/v1/destinacoes-contribuicao`

### Recursos
- `POST /api/v1/recursos-vinculados`
- `GET /api/v1/recursos-vinculados`
- `GET /api/v1/recursos-vinculados/{uuid}`
- `POST /api/v1/recursos-vinculados/utilizacoes`

## Banco

Migration: `V7__projeto_recurso_vinculado.sql`.

Tabelas novas:
- `projeto`
- `recurso_vinculado`
- `fonte_recurso_projeto`
- `utilizacao_recurso`

Não foram adicionados triggers.
Não foram adicionados campos de saldo/realizado derivados.

## Fora da v16.6

- cronograma e etapas da obra;
- medições físicas;
- aprovação específica de projeto;
- compras e contratos;
- prestação de contas avançada;
- motor automático de repasses;
- reconciliação bancária avançada.
