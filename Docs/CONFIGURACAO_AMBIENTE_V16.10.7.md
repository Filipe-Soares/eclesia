# Configuração de Ambiente — v16.10.7

## Administrador inicial
- `ADM_ECLESIAL_ADMIN_PASSWORD`: senha do bootstrap inicial. Não deve ser hardcoded.
- `ADM_ECLESIAL_ADMIN_LOGIN`: login inicial opcional, conforme suporte já existente no projeto.

O bootstrap somente deve ocorrer conforme as condições implementadas na aplicação (ausência de usuários, organização ativa e senha configurada).

## Banco de dados
Use configuração externa apropriada ao ambiente para URL, usuário e senha do PostgreSQL.
Valores locais/default servem apenas para desenvolvimento e não devem ser reutilizados como segredo de produção.

## Thymeleaf
Ajustes de cache devem ser definidos de acordo com o ambiente; desenvolvimento e produção têm necessidades diferentes.

## Testes
Testes de integração podem exigir Docker por causa do Testcontainers/PostgreSQL.
