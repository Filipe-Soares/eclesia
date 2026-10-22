# ADM Eclesial v16.8.3 — Dízimos / Contribuições

- Rota: `/relatorios/contribuicoes`
- Aba padrão: **Contribuições**
- Aba complementar: **Por Dizimista**
- Filtros: organização, período, tipo, categoria e pessoa.
- Totalizadores da visão padrão: quantidade de contribuições e valor total sobre todo o conjunto filtrado.
- Contribuições sem pessoa são exibidas como `Anônima / não identificada`.
- Destinações são agregadas para manter uma linha por contribuição.
- Visão Por Dizimista consolida quantidade, valor total e última contribuição.
- Segurança: somente `ADMINISTRADOR` e `FINANCEIRO`, além da validação da organização autorizada.
- Sem migration e sem novas tabelas de relatório.

Validar com:

```bash
mvn clean test
```
