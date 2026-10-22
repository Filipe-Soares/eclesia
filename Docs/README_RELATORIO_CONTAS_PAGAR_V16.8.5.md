# ADM Eclesial v16.8.5 — Relatório de Contas a Pagar

- Rota: `/relatorios/contas-a-pagar`
- Perfis: `ADMINISTRADOR` e `FINANCEIRO`.
- Organização obrigatória e validada pelo `SegurancaOrganizacaoService`.
- Ordenação padrão: vencimento mais próximo (`ASC`).
- Filtros: organização, período de vencimento, fornecedor, situação e `Somente vencidas`.
- Totais derivados: valor total, valor pago e saldo a pagar.
- Pagamentos são derivados de `ALOCACAO_PAGAMENTO`; não há `valor_pago`/`saldo_devedor` persistidos.
- Uma conta é destacada como vencida quando possui parcela não cancelada, vencida antes de `current_date`, com saldo remanescente, e a conta não está cancelada.
- Contas originadas de `OBRIGACAO_REPASSE` são identificadas como repasse, sem duplicar o relatório específico de repasses.
- Nenhuma migration nova.

Validação local: `mvn clean test`.
