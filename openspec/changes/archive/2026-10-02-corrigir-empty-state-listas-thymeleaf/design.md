## Context

Ver `proposal.md` para motivação do problema de precedência do Thymeleaf entre `th:replace` e `th:if`.

## Goals / Non-Goals

**Goals:**
- Utilizar a tag estrutural `<th:block th:if="...">` ao redor das chamadas do fragmento de empty-state em todas as telas de listagem HTML.
- Preservar todo o CSS, classes Bootstrap e estrutura de layout existente.
- Garantir que a suíte de testes continue passando com 100% de sucesso.

**Non-Goals:**
- Não alterar lógica de negócio, controladores Java ou consultas JPA.

## Decisions

1. **Decisão 1: Utilização de `<th:block th:if="...">`**
   - *Alternativa A:* Mudar o fragmento em `layout.html` para incluir `th:if`. (Descartado: tiraria a flexibilidade de nomes de variáveis como `pessoas`, `organizacoes`, `vinculos`, etc.)
   - *Alternativa B (Escolhida):* Envolver a chamada do fragmento no template chamador com `<th:block th:if="${#lists.isEmpty(colecao)}">`. É a abordagem canônica e recomendada pelo Thymeleaf para isolar escopo condicional antes de substituições de nó.

## Risks / Trade-offs

- [Risco] Omitir algum template de listagem menos utilizado.
- [Mitigação] Varredura automatizada com grep em todos os arquivos `.html` dentro de `src/main/resources/templates/`.
