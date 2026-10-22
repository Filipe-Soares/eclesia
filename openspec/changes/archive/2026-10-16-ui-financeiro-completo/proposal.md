## Why

Todas as operações financeiras do MVP (contas, categorias, centros de custo, fornecedores, dizimistas, movimentações, transferências, contas a pagar, pagamentos, repasses e orçamento) já existem nos Application Services e nas rotas REST, mas a interface Thymeleaf expõe apenas uma visão de leitura (`financeiro/index.html`). Operadores do perfil FINANCEIRO não conseguem executar o fluxo operacional descrito no manual (§18.7–18.13) sem usar a API diretamente.

## What Changes

- Expor na interface Thymeleaf as operações financeiras hoje disponíveis apenas via REST, reutilizando os Application Services e regras de negócio existentes:
  - Contas financeiras (criar/editar/listar)
  - Categorias financeiras (criar/listar)
  - Centros de custo (criar/listar)
  - Fornecedores (criar/listar)
  - Dizimistas (criar/editar/listar)
  - Movimentações financeiras (registrar/listar)
  - Transferências financeiras (criar/listar)
  - Contas a pagar (criar/listar, com parcela gerada automaticamente)
  - Pagamentos (criar com alocações/listar)
  - Regras de repasse (criar/listar)
  - Obrigações de repasse (gerar/listar)
  - Orçamento (criar, incluir itens, aprovar, encerrar, cancelar e listar)
- Adicionar formulários, telas de listagem e navegação em `templates/financeiro/`, partindo da visão geral atual.
- Reutilizar os padrões de UI já existentes (lista/form, fragmentos de layout, feedback de erro de negócio e proteção por perfil).
- **Sem alterações de regras de negócio, schema, migrations ou Application Services.**

## Capabilities

### New Capabilities
- `ui-financeiro`: Interface Thymeleaf para operação do módulo financeiro (contas, categorias, centros de custo, fornecedores, dizimistas, movimentações, transferências, contas a pagar, pagamentos, repasses e orçamento), respeitando as regras de negócio e o isolamento organizacional já implementados no backend.

### Modified Capabilities
<!-- Nenhuma: os requisitos de backend permanecem inalterados; apenas a camada de apresentação é adicionada. -->

## Impact

- Interfaces Web: `FinanceiroWebController` e novos controllers web em `interfaces/web/financeiro/`.
- Templates Thymeleaf: `templates/financeiro/` (novos `lista.html`/`form.html`/`detalhe.html` por recurso e ajustes em `index.html`) e navegação em `templates/fragments/layout.html`.
- Segurança: rotas protegidas por perfil ADMINISTRADOR/FINANCEIRO, mantendo CSRF e o isolamento de organização já aplicado nos services.
- Application Services e REST: reutilizados sem alteração.
- Testes: novos testes de controller web/mockMvc para os fluxos expostos.
