# Matrimonio e Financeiro — v16

## Matrimonio

Fluxo implementado:

```text
PROCESSO_MATRIMONIAL
        |
        v
CELEBRACAO_MATRIMONIAL
        |
        v
MATRIMONIO
```

### Processo matrimonial
- organização eclesial
- dois nubentes
- abertura/encerramento
- status
- observação

### Celebração
- processo
- organização
- celebrante
- data
- local
- observação

### Registro
- processo
- celebração
- data do registro
- observação

REST:
- `/api/v1/processos-matrimoniais`
- `/api/v1/celebracoes-matrimoniais`
- `/api/v1/matrimonios`

Thymeleaf:
- `/processos-matrimoniais`
- `/processos-matrimoniais/novo`
- `/processos-matrimoniais/{uuid}`
- `/processos-matrimoniais/{uuid}/celebracao`
- `/processos-matrimoniais/{uuid}/registro`

## Financeiro operacional

Fluxo principal:

```text
CONTA_FINANCEIRA
      |
      v
MOVIMENTACAO_FINANCEIRA

CONTRIBUICAO
      |
      v
CONTRIBUICAO_MOVIMENTACAO

REGRA_REPASSE
      |
      v
OBRIGACAO_REPASSE
      |
      v
CONTA_A_PAGAR
      |
      v
PARCELA_A_PAGAR
      |
      v
PAGAMENTO
      |
      v
PAGAMENTO_MOVIMENTACAO
```

Incluídos no código:
- contas financeiras
- categorias
- centros de custo
- movimentações
- transferências (entidade/repositório)
- dizimistas (entidade/repositório)
- contribuições
- destinações de contribuição
- rastreio contribuição → movimentação
- fornecedores
- regras de repasse
- obrigações de repasse
- contas a pagar
- parcelas
- pagamentos
- alocações de pagamento
- rastreio pagamento → movimentação

A regra de repasse preserva `valor_base`, `valor_exclusoes` e `valor_repasse` como snapshot histórico.

### Fora desta entrega
Continuam deliberadamente posteriores:
- orçamento
- projeto/obra
- recurso vinculado formal
- compras
- contratos
- aprovação financeira avançada
- prestação de contas avançada
- auditoria avançada
- engine sofisticado de cálculo de repasse
- conciliação bancária avançada

Esses itens não foram adicionados artificialmente ao MVP.


## v16.4 — integração Matrimonio ↔ Sacramento

- `MATRIMONIO.sacramento_id` relaciona o matrimônio ao Sacramento correspondente.
- O fluxo de Matrimonio cria automaticamente um `SACRAMENTO` do tipo `MATRIMONIO`.
- Os dois nubentes são registrados como `CONJUGE` no Sacramento.
- A criação genérica de Sacramento do tipo `MATRIMONIO` é bloqueada.
- A celebração deve pertencer à mesma organização do processo matrimonial.
- Processo matrimonial passa a validar transições básicas de status.
- Ao concluir o matrimônio, o processo recebe `CONCLUIDO` e data de encerramento, se ainda não informada.


## v16.4 — fechamento do fluxo financeiro MVP

- Contribuição exige conta financeira e gera `MOVIMENTACAO_FINANCEIRA` de entrada + `CONTRIBUICAO_MOVIMENTACAO`.
- Dízimo valida `DIZIMISTA`, pessoa, organização e vigência.
- `DIZIMISTA` ganhou CRUD REST básico.
- Conta a pagar passa a criar automaticamente a primeira parcela.
- Obrigação de repasse passa a criar automaticamente `CONTA_A_PAGAR` + parcela.
- Pagamento exige alocações completas às parcelas, gera `PAGAMENTO_MOVIMENTACAO` e saída financeira, e atualiza status de parcela/conta.
- Transferência financeira gera duas movimentações vinculadas.
- Validação de centro de custo contra a organização da conta financeira.
- Percentual de regra de repasse limitado a 0–100.

## v16.5 — revisão financeira e orçamento

### Correções da revisão técnica
- Contribuição não aceita categoria financeira inativa.
- Contribuição não-DIZIMO não aceita `dizimistaUuid`.
- Pagamento não pode utilizar conta a pagar cancelada.
- Movimentação manual não aceita `TRANSFERENCIA`; transferências devem passar pelo fluxo próprio.
- Regra de repasse exige somente o parâmetro aplicável ao tipo de base (percentual ou valor fixo).

### Próxima funcionalidade: Orçamento
Novo fluxo:

```text
ORCAMENTO
   |
   +--> ITEM_ORCAMENTO --> CATEGORIA_FINANCEIRA
                  |
                  +-----> CENTRO_CUSTO (opcional)
```

Regras do MVP:
- um orçamento por organização e ano;
- orçamento começa como `PLANEJADO`;
- itens só podem ser adicionados enquanto planejado;
- categoria deve estar ativa;
- centro de custo, quando informado, deve pertencer à organização do orçamento;
- aprovação exige pelo menos um item;
- após aprovado, não recebe novos itens;
- pode ser encerrado somente após aprovação;
- orçamento encerrado não pode ser cancelado;
- nenhum total realizado é armazenado: a execução futura será calculada a partir das movimentações financeiras.

REST:
- `POST /api/v1/orcamentos`
- `GET /api/v1/orcamentos`
- `GET /api/v1/orcamentos/{uuid}`
- `POST /api/v1/orcamentos/{uuid}/itens`
- `GET /api/v1/orcamentos/{uuid}/itens`
- `POST /api/v1/orcamentos/{uuid}/aprovar`
- `POST /api/v1/orcamentos/{uuid}/encerrar`
- `POST /api/v1/orcamentos/{uuid}/cancelar`
