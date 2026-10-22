## Why

A modernização anterior com Bootstrap 5.3 contemplou o layout base compartilhado, as telas de Dashboard e a tela de login. No entanto, os módulos operacionais de cadastros gerais (Pessoas, Organizações, Usuários, Funções, Vínculos), Pastoral (Catequese, Sacramentos, Matrimônios), Financeiro/Projetos, Relatórios e páginas de Erro ainda utilizavam marcação HTML crua sem inclusão do menu lateral responsivo, cabeçalho e componentes modernos do Bootstrap.
A expansão da padronização visual para 100% das páginas do sistema entregará uma experiência de usuário (UX/UI) coesa, profissional e responsiva, mantendo intactas todas as regras de negócio, contratos de dados e permissões do Spring Security.

## What Changes

- Aplicação do layout padrão (`fragments/layout :: assets`, `header`, `sidebar`, `feedback` e `empty-state`) em todas as páginas:
  - **Cadastros Gerais:** `pessoa/` (lista, form, detalhe), `organizacao/` (lista, form, detalhe), `usuario/` (lista, form), `funcao/` (lista, form), `funcao-vinculo/` (form), `vinculo/` (form).
  - **Pastoral e Sacramentos:** `catequese/` (lista, form, detalhe), `turma-catequese/` (lista, form, detalhe), `etapa-catequese/` (lista, form), `inscricao-catequese/` (lista, form), `catequista-turma/` (lista, form), `sacramento/` (lista, form, detalhe), `registro-sacramental/` (lista, form, detalhe), `participante-sacramento/` (lista, form), `matrimonio/` (lista, form, detalhe).
  - **Financeiro & Projetos:** `financeiro/` (telas operacionais) e `projeto/` (lista, form, detalhe).
  - **Relatórios (8 telas):** `relatorio/` (`pessoas.html`, `catequese.html`, `sacramentos.html`, `contribuicoes.html`, `movimentacoes-financeiras.html`, `contas-a-pagar.html`, `repasses-curia.html`, `projetos-obras.html`).
  - **Páginas de Erro e Segurança:** `error/403.html`, `error/404.html`, `error/500.html`, `error/error.html` e `seguranca/acesso-negado.html`.
- Estilização de todas as tabelas com `table table-hover table-striped align-middle`, badges contextuais para status/situação e formulários com classes de grid e validação do Bootstrap.

## Capabilities

### New Capabilities
- `ui-bootstrap-completo`: Padronização visual completa de todas as telas operacionais, formulários, tabelas de listagem, relatórios e páginas de erro do ADM Eclesial com Bootstrap 5.3.

### Modified Capabilities
<!-- Nenhuma especificação de regra de negócio existente foi alterada. -->

## Impact

- **Código/Templates:** Modificação de todos os arquivos HTML em `src/main/resources/templates/`.
- **Backend:** Zero impacto nos Controllers, Services, Repositories, DTOs e regras de validação.
