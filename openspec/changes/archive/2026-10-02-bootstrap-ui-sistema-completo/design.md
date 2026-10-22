## Context

O layout mestre (`fragments/layout.html`) já possui os fragmentos `assets`, `header`, `sidebar`, `feedback` e `empty-state` estilizados com Bootstrap 5.3 e Bootstrap Icons. Agora, todas as páginas operacionais restantes devem ser refatoradas para usar essa base comum. Veja `proposal.md` para motivação.

## Goals / Non-Goals

**Goals:**
- Enquadrar todas as páginas HTML de `templates/` no container padrão `<div class="app-shell"><aside th:replace="~{fragments/layout :: sidebar}"></aside><main class="app-main"><div class="page">...</div></main></div>`.
- Padronizar cabeçalhos de tela (`d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom`).
- Padronizar formulários em cards com grid (`row g-3`), labels e botões de ação (`btn btn-eclesial` e `btn btn-outline-secondary`).
- Padronizar tabelas com `table-responsive` e classes Bootstrap.
- Padronizar relatórios com cards de filtros e paginação.
- Padronizar telas de erro em cards centralizados com ícones.

**Non-Goals:**
- Não alterar regras de negócio, controladores Spring MVC, DTOs, entidades ou permissões do Spring Security.
- Não introduzir frameworks client-side JavaScript.

## Decisions

1. **Padronização Estrutural Unificada:**
   - *Decisão:* Todas as páginas operacionais herdam `<head th:replace="~{fragments/layout :: assets('Título da Página')}">` e o layout shell.
2. **Componentes de Ação e Feedback:**
   - *Decisão:* Botões principais utilizam `btn btn-eclesial` (azul institucional), botões de cancelamento `btn btn-outline-secondary` e ações de exclusão/alerta `btn btn-outline-danger`.

## Risks / Trade-offs

- **[Risco] Esquecimento de algum atributo Thymeleaf (`th:field`, `th:value`, `th:action`) durante a refatoração:**
  - *Mitigação:* Preservar exatamente os mesmos nomes de campos e métodos HTTP, validando cada etapa com a suíte de testes `mvn test`.
