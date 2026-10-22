# v16.8.6 — Relatório de Repasses para Cúria

- Rota: `/relatorios/repasses-curia`
- Perfis: ADMINISTRADOR e FINANCEIRO.
- Organização obrigatória e validada pelo `SegurancaOrganizacaoService`.
- Filtros: organização, intervalo de vencimento, status e `Somente vencidos`.
- `Somente vencidos` inicia ativo por padrão.
- Ordenação: vencimento ASC.
- Totalizadores: total devido, total pago e saldo a repassar, sempre sobre todo o conjunto filtrado.
- Vencido: vencimento anterior à data atual, saldo > 0 e obrigação não cancelada.
- Pago/saldo derivados de conta/parcela/alocação; nenhum total redundante é persistido.
- Sem migration nova.
