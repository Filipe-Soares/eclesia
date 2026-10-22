# ADM Eclesial v16.10.1 — Layout, Menu e Navegação

## Objetivo
Primeira entrega da fase final de UX do MVP, sobre a baseline v16.9.6 validada.

## Entregue
- fragmento Thymeleaf compartilhado para cabeçalho e menu lateral;
- identificação do usuário autenticado e logout com POST/CSRF;
- menu condicionado às authorities do Spring Security;
- união natural dos módulos para usuários com múltiplos perfis;
- dashboard continua escolhendo a visão pela precedência definida na v16.9.6;
- CSS global responsivo em `/css/app.css`;
- cinco dashboards migrados para o novo shell visual;
- links antigos inexistentes de turmas/inscrições removidos do dashboard Catequese;
- atalhos do menu limitados a rotas existentes e permissões já configuradas.

## Sem alteração
- nenhuma migration;
- nenhuma tabela nova;
- nenhuma regra de negócio alterada;
- `FUNCAO_ECLESIAL` não participa da autorização.

## Estratégia de UX
A migração visual é incremental para reduzir regressões no fim do MVP. Os fragments e o CSS global criados nesta versão serão reutilizados na padronização das demais telas nas próximas entregas v16.10.x.
