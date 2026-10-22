# ADM Eclesial v16.9.3 — Dashboard SECRETARIA

## Escopo
Dashboard operacional para o perfil `SECRETARIA`, sem exposição de dados financeiros.

### Contexto
- organização limitada às organizações autorizadas do usuário;
- período padrão: mês atual;
- período pode ser alterado por início/fim.

### Cards
- Pessoas ativas na organização;
- Novos cadastros no período;
- Sacramentos registrados no período (`SACRAMENTO.data_registro`);
- Matrimônios registrados no período (`MATRIMONIO.data_registro`);
- Turmas ativas.

### Alerta
- Turmas ativas sem catequista principal vigente.

### Segurança
`GET /dashboard` agora admite `ADMINISTRADOR`, `FINANCEIRO` e `SECRETARIA`. O controller entrega uma view/DTO específico por perfil. `SECRETARIA` não recebe campos financeiros.

### Banco
Nenhuma migration e nenhuma tabela de dashboard/notificação foram adicionadas.
