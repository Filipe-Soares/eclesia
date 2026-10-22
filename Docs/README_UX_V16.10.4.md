# ADM Eclesial — v16.10.4

## Responsividade + consistência visual

Baseline: v16.10.3 validada com `BUILD SUCCESS`.

### Responsividade
- navegação, cabeçalho e conteúdo ajustados para telas menores;
- cards passam para uma coluna em smartphones;
- filtros e formulários passam para uma coluna quando necessário;
- ações de formulário quebram de linha e preservam área de toque;
- tabelas passam a usar contêiner horizontal (`.table-responsive`) em telas estreitas, evitando compressão ilegível;
- páginas de erro e estados vazios também foram refinados para mobile.

### Consistência visual
- foco de teclado visível em links, botões e campos;
- padrões compartilhados para tabelas, ações, filtros, paginação, badges/status, painéis e toolbars;
- espaçamentos e tipografia responsivos;
- suporte a `prefers-reduced-motion`;
- compatibilidade mantida com classes legadas (`.grid`, `.filters`, `.actions`, `.cards`).

### Escopo preservado
- nenhuma migration;
- nenhuma tabela nova;
- nenhuma alteração de regra de negócio;
- nenhuma alteração na matriz de permissões;
- nenhuma dependência front-end nova.
