# ADM Eclesial v16.7 — Segurança do MVP

## Escopo
- Spring Security com sessão HTTP
- BCrypt
- Perfis: ADMINISTRADOR, SECRETARIA, CATEQUESE, FINANCEIRO, CONSULTA
- Perfis acumuláveis por usuário
- Organizações autorizadas por usuário
- CSRF permanece habilitado
- Sem JWT/OAuth2/MFA/ACL no MVP

## Primeiro administrador
O bootstrap não possui senha padrão. Na primeira execução, se não houver usuários e existir ao menos uma organização ativa, defina:

```bash
set ADM_ECLESIAL_ADMIN_PASSWORD=uma-senha-forte
```

Opcionalmente:
```bash
set ADM_ECLESIAL_ADMIN_LOGIN=admin
```

O bootstrap cria uma pessoa técnica `Administrador do Sistema`, o usuário ADMINISTRADOR e vincula a primeira organização ativa. Após existir qualquer usuário, o bootstrap não executa novamente.

## Segurança por organização
`SegurancaOrganizacaoService` centraliza a verificação da organização do usuário. Os próximos relatórios devem obrigatoriamente consultar apenas organizações autorizadas. A proteção por URL/perfil não substitui essa verificação para dados multi-organização.
