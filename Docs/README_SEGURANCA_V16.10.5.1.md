# v16.10.5.1 — Isolamento fino por organização

Hardening sem alteração de banco ou domínio.

## Aplicado
- `SegurancaOrganizacaoService` centraliza o conjunto de UUIDs autorizados do usuário autenticado.
- Organização Eclesial: listagem limitada ao escopo autorizado e detalhe protegido contra acesso direto por UUID.
- Catequese: listagem e combo de organizações limitados ao escopo; criação, edição, atualização e detalhe validam a organização.
- Vínculo Pessoa–Organização: listagem e combo limitados ao escopo; criação e detalhe validam a organização.
- ADMINISTRADOR continua sem bypass global: o acesso depende de `USUARIO_ORGANIZACAO`.
- Testes negativos A→B adicionados ao serviço de segurança.

## Limite arquitetural preservado
`PESSOA` não recebeu `organizacao_id`. Pessoa continua sendo entidade compartilhável e sua associação organizacional permanece em `VINCULO_PESSOA_ORGANIZACAO_ECLESIAL`. Catálogos globais, como `FUNCAO_ECLESIAL`, também não foram artificialmente transformados em dados por organização.

## Banco
Nenhuma migration, trigger, tabela ou coluna nova.
