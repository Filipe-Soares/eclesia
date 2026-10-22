# ADM Eclesial — v16.9.1 Dashboard Administrador

## Escopo
- Dashboard inicial do perfil ADMINISTRADOR em `/dashboard`.
- Organização limitada às organizações autorizadas do usuário.
- Período padrão: primeiro ao último dia do mês atual.
- Período pode ser personalizado por data inicial/final.
- Sem migrations e sem tabelas de consolidação.

## Cards operacionais
- Pessoas ativas
- Turmas ativas
- Inscritos ativos
- Projetos em execução
- Sacramentos no período

## Cards financeiros
- Entradas confirmadas
- Saídas confirmadas
- Resultado
- Quantidade e valor de contribuições no período

## Alertas
- Contas vencidas e saldo vencido
- Repasses para Cúria vencidos e saldo a repassar
- Turmas ativas sem catequista principal
- Projetos com recurso vinculado sem utilização

Os valores são derivados das tabelas existentes. Nenhum saldo ou total redundante é persistido.

## Segurança
`/dashboard` exige `ROLE_ADMINISTRADOR`. O serviço também valida a organização com `SegurancaOrganizacaoService`.

## Validação
Execute:

```bash
mvn clean test
```
