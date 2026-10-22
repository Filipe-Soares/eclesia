## Why

Ambientes de desenvolvimento e novas instalações do ADM Eclesial inicializam com o banco de dados vazio, sem nenhuma organização eclesial ativa nem usuário cadastrado. Como o `AdminBootstrap` exige uma organização ativa pré-existente e as telas de cadastro exigem autenticação prévia, o sistema fica inacessível para o primeiro login.
A introdução de uma migration Flyway de seed provisionará a organização inicial e o usuário administrador padrão (`admin` / `admin123`), viabilizando o primeiro acesso de forma transparente e reprodutível.

## What Changes

- Criação da migration Flyway `src/main/resources/db/migration/V9__seed_inicial_admin.sql` contendo:
  - Inserção de uma organização eclesial ativa (`Paróquia Matriz - Sede`).
  - Inserção do registro de pessoa associado (`Administrador do Sistema`).
  - Inserção do usuário `admin` com hash BCrypt para a senha `admin123`.
  - Atribuição do perfil `ADMINISTRADOR` na tabela `usuario_perfil`.
  - Associação à organização criada na tabela `usuario_organizacao`.
- Garantia de idempotência e compatibilidade com PostgreSQL e Flyway.

## Capabilities

### New Capabilities
- `seed-admin`: Provisionamento automático via Flyway de organização inicial e usuário administrador para primeiro acesso.

### Modified Capabilities
<!-- Nenhuma especificação de regra de negócio existente foi alterada. -->

## Impact

- **Banco de Dados:** Execução da migration Flyway `V9__seed_inicial_admin.sql`.
- **Código:** Nenhuma alteração de regras Java ou APIs.
