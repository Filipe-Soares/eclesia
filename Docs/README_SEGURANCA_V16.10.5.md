# v16.10.5 — Revisão de Segurança e Permissões

## Escopo
Revisão de rotas/autorização e isolamento por organização, sem mudança de domínio ou banco.

## Correções de rotas
O `SecurityConfig` usava aliases antigos (`/organizacoes/**`, `/funcoes/**`, `/vinculos/**`) enquanto os controllers Web reais usam:

- `/organizacoes-eclesiais/**`
- `/funcoes-eclesiais/**`
- `/vinculos-pessoa-organizacao/**`
- `/vinculos-funcoes-eclesiais/**`

A matriz foi alinhada às rotas reais. A autorização passa a ocorrer no backend mesmo quando a URL é digitada diretamente.

## Matriz preservada
- Organização: leitura autenticada; alteração somente ADMINISTRADOR.
- Funções/vínculos: leitura autenticada; alteração ADMINISTRADOR ou SECRETARIA.
- Catequese: leitura ADMINISTRADOR/SECRETARIA/CATEQUESE/CONSULTA; alteração ADMINISTRADOR/CATEQUESE.
- Sacramentos: leitura ADMINISTRADOR/SECRETARIA/CATEQUESE/CONSULTA; alteração ADMINISTRADOR/SECRETARIA.
- Matrimonio: leitura ADMINISTRADOR/SECRETARIA/CONSULTA; alteração ADMINISTRADOR/SECRETARIA.
- Financeiro: ADMINISTRADOR/FINANCEIRO.
- Projetos: leitura ADMINISTRADOR/SECRETARIA/FINANCEIRO/CONSULTA; alteração ADMINISTRADOR/FINANCEIRO.
- Pessoas: leitura nos cinco perfis; alteração ADMINISTRADOR/SECRETARIA.

## Isolamento organizacional
O mecanismo oficial continua sendo `SegurancaOrganizacaoService`, baseado em usuário autenticado ativo + `USUARIO_ORGANIZACAO`.
Dashboard e relatórios já validam `organizacaoUuid` antes das consultas. Esta revisão confirmou esse comportamento e não substitui a validação por ocultação de menu.

### Observação de escopo
Cadastros operacionais legados que listam dados globalmente ainda devem ser tratados em uma futura evolução de isolamento fino por entidade/consulta. A v16.10.5 não afirma isolamento multi-tenant completo: ela fecha a matriz de rotas e preserva/valida o isolamento explícito já existente em dashboards e relatórios.

## CSRF
Permanece ativo. Não foi desabilitado para REST nem para formulários Web.

## Sem migration
Nenhuma tabela, coluna, enum de banco ou migration foi adicionada.
