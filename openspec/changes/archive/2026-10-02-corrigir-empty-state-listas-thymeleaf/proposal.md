## Why

Nas telas de listagem do sistema (Pessoas, Organizações Eclesiais, Vínculos, etc.), a diretiva `th:replace="~{fragments/layout :: empty-state(...)}"` possui precedência maior que a diretiva `th:if`, fazendo com que a mensagem de "Nenhum registro encontrado" seja renderizada incondicionalmente, mesmo quando há registros cadastrados e a tabela com os dados é exibida logo abaixo.

## What Changes

- Envolver as chamadas do fragmento `empty-state` com `<th:block th:if="${#lists.isEmpty(...)}">` em todos os templates de listagem do sistema.
- Garantir que a mensagem de lista vazia seja exibida estritamente quando a coleção estiver vazia, e ocultada quando houver registros.
- Manter a exibição das tabelas de listagem intacta e responsiva.

## Capabilities

### New Capabilities
- `ui-listas`: Padronização da renderização condicional de estados vazios (empty-state) nas telas de listagem do Thymeleaf.

### Modified Capabilities
<!-- None -->

## Impact

- Templates Thymeleaf em `src/main/resources/templates/**/lista.html` e correlatos.
- Não há alterações em modelos de domínio ou banco de dados.
