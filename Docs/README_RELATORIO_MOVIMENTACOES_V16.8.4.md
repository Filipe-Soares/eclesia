# v16.8.4 — Relatório de Movimentações Financeiras

- Rota: `/relatorios/movimentacoes-financeiras`
- Ordem padrão: data da movimentação DESC.
- Filtros: organização, período, conta, tipo, categoria e centro de custo.
- Totalizadores: entradas confirmadas, saídas confirmadas e resultado (entradas - saídas).
- Transferências internas (`tipo=TRANSFERENCIA`) permanecem na listagem, mas não entram nos totalizadores.
- Movimentações pendentes/canceladas permanecem visíveis na listagem, mas não entram nos totalizadores.
- Segurança: ADMINISTRADOR e FINANCEIRO, com validação da organização autorizada.
- Sem migration e sem totais persistidos.
