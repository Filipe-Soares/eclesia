# Revisão técnica do módulo financeiro — v16.5

## Resultado

O fluxo financeiro MVP foi considerado estruturalmente adequado para a próxima etapa. Não foram adicionados triggers, saldos materializados ou tabelas de auditoria desnecessárias.

## Pontos verificados

| Área | Situação | Decisão |
|---|---|---|
| Conta financeira → movimentação | OK | Mantido |
| Contribuição → movimentação | OK | Mantido |
| Dízimo → dizimista | OK | Mantido |
| Conta a pagar → parcelas | OK | Mantido |
| Pagamento → alocação → movimentação | OK | Mantido |
| Transferência entre contas | OK | Fluxo próprio mantido |
| Regra → obrigação de repasse | OK | Snapshot de valores mantido |
| Categoria inativa em contribuição | Lacuna | Corrigido |
| Dizimista informado em contribuição não-DIZIMO | Lacuna | Corrigido |
| Pagamento de conta cancelada | Lacuna | Corrigido |
| Transferência criada como movimentação manual | Risco | Bloqueado |
| Parâmetros incompatíveis na regra de repasse | Lacuna | Corrigido |
| Destinação de contribuição | Parcial | Modelo existe; fluxo operacional permanece posterior |
| Conciliação bancária | Posterior | Não entra no MVP |
| Fechamento de período | Posterior | Não entra no MVP |
| Repasse com entrada automática no destino | Posterior | Não duplicar movimento neste MVP |

## Decisões importantes

### Saldos
Não foi criada coluna `saldo_atual`. O saldo deve ser calculado a partir das movimentações confirmadas da conta.

### Contas a pagar
Não foram criados campos redundantes como `valor_pago` ou `saldo_devedor`. A posição financeira é derivada das alocações das parcelas.

### Orçamento
Foi escolhido como próxima funcionalidade porque utiliza diretamente categorias, centros de custo e movimentações, preparando a futura análise planejado × realizado.

## Próxima evolução

A execução real do orçamento deverá ser calculada posteriormente a partir de `MOVIMENTACAO_FINANCEIRA`, respeitando organização, categoria, centro de custo, período e status da movimentação. O banco não armazenará um total realizado duplicado.
