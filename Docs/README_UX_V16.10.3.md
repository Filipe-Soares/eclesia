# ADM Eclesial v16.10.3 — Páginas de erro e estados vazios

Baseline: v16.10.2 validada pelo usuário com BUILD SUCCESS.

## Páginas de erro
- 403: acesso negado com linguagem amigável e navegação segura.
- 404: página/recurso não encontrado.
- 500: erro interno sem exposição de detalhes técnicos.
- fallback `error/error.html` para erros não especializados.
- `server.error` configurado para não incluir exception, stack trace, binding errors ou mensagem interna.
- página `/acesso-negado` do Spring Security alinhada visualmente ao 403.

## Estados vazios
Estados vazios contextuais adicionados/padronizados nas listagens principais:
- Pessoas
- Organizações eclesiais
- Funções eclesiais
- Vínculos pessoa × organização
- Catequese
- Etapas
- Turmas
- Inscrições
- Sacramentos
- Processos matrimoniais
- Usuários
- Projetos / Obras

As tabelas principais sem dados deixam de exibir apenas cabeçalhos vazios nos templates atualizados.

## Infraestrutura visual
- estilos `.error-page`, `.error-card`, `.empty-state` e relacionados em `static/css/app.css`;
- fragmento reutilizável `empty-state` incluído em `fragments/layout.html` para adoção incremental.

## Fora do escopo
- nenhuma migration;
- nenhuma nova tabela;
- nenhuma alteração de regra de negócio;
- nenhuma alteração de modelo de segurança/perfis.

## Validação
Executar:

```bash
mvn clean test
```
