## Why

Ao acessar rotas de listagem como `/processos-matrimoniais`, `/sacramentos`, `/projetos`, `/vinculos-pessoa-organizacao`, entre outras, ocorre o erro do Thymeleaf `Cannot resolve fragment. Signature "empty-state (title,message)" declares 2 parameters, but fragment selection specifies 1 parameters`. É necessário corrigir a assinatura do fragmento `empty-state` em `fragments/layout.html` e padronizar as chamadas em todas as páginas para garantir renderização correta sem erros de parsing.

## What Changes

- Padronizar o fragmento `empty-state` em `templates/fragments/layout.html` para permitir uso flexível ou com parâmetros corretos.
- Atualizar todas as chamadas `th:replace="~{fragments/layout :: empty-state(...)}"` em todas as páginas de listagem (`matrimonio/processo-lista.html`, `sacramento/lista.html`, `projeto/lista.html`, `vinculo/lista.html`, `funcao-vinculo/lista.html`, `participante-sacramento/lista.html`, `inscricao-catequese/lista.html`, `etapa-catequese/lista.html`, `catequista-turma/lista.html`) para passar tanto o título quanto a mensagem explicativa (`title, message`).
- Adicionar testes de renderização web/controller para validar que todas as páginas de listagem renderizam sem erros de fragmento Thymeleaf.

## Capabilities

### Modified Capabilities
- `ui-bootstrap-completo`: Garantir que os componentes de empty state sejam renderizados sem falhas de assinatura em todas as listagens do sistema.

## Impact

- `src/main/resources/templates/fragments/layout.html`
- Templates em `src/main/resources/templates/**/lista.html` e `matrimonio/processo-lista.html`
- Testes automatizados web controllers
