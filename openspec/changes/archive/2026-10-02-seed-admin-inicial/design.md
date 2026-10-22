## Context

O sistema utiliza Flyway para versionamento do esquema de banco de dados (`src/main/resources/db/migration/`). As versões V1 até V8 definiram as tabelas e índices. A versão V9 realizará o seed inicial. Veja `proposal.md` para motivação.

## Goals / Non-Goals

**Goals:**
- Criar a migration `V9__seed_inicial_admin.sql` no schema `adm_eclesial`.
- Inserir a organização com UUID estático ou gerado, tipo `PAROQUIA`, ativa.
- Inserir a pessoa `Administrador do Sistema`.
- Inserir o usuário `admin` com hash BCrypt válido de `admin123` (`$2a$10$iI8j9/dNu5sN5yL8e7p0/OMUaO4bU.3/zXjF5Mh6tL4PqvHjI1ZKi` ou equivalente gerado por BCrypt).
- Inserir os registros nas tabelas `usuario_perfil` e `usuario_organizacao`.

**Non-Goals:**
- Não sobrescrever usuários ou organizações existentes caso já tenham sido cadastrados de forma personalizada em bancos de produção.

## Decisions

1. **Seed via Migration Flyway (V9):**
   - *Decisão:* Usar script SQL executado na ordem cronológica de migrations do Flyway.
   - *Alternativas consideradas:* CommandLineRunner Java (descartado para evitar dependências de ciclo de vida e garantir consistência direta no banco).

2. **Hash BCrypt da Senha `admin123`:**
   - *Decisão:* Utilizar hash BCrypt padrão de 10 rounds para a string `admin123`.

## Risks / Trade-offs

- **[Risco] Tentativa de inserção duplicada:**
  - *Mitigação:* Usar `WHERE NOT EXISTS` ou `ON CONFLICT DO NOTHING` nas cláusulas SQL para garantir total idempotência caso o script seja executado em bancos já populados.
