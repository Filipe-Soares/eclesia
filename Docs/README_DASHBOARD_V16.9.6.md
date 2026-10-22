# v16.9.6 — Integração e ajustes finais do Dashboard

Consolidação da fase v16.9, sem migration e sem novas tabelas.

## Ajustes
- mantém as cinco visões: ADMINISTRADOR, FINANCEIRO, SECRETARIA, CATEQUESE e CONSULTA;
- precedência explícita para usuários com perfis acumulados: ADMINISTRADOR > FINANCEIRO > SECRETARIA > CATEQUESE > CONSULTA;
- teste unitário da precedência, independente da ordem das authorities do Spring Security;
- organização padrão sempre vem da lista de organizações autorizadas do usuário;
- UUID de organização recebido pela URL é validado no backend antes da montagem do dashboard;
- período padrão continua sendo o mês atual;
- período final anterior ao inicial é rejeitado;
- nenhum fallback para organização global;
- nenhuma tabela de dashboard, alerta ou total consolidado foi criada.

## Validação
Executar:

```bash
mvn clean test
```
