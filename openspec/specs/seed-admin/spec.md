# seed-admin Specification

## Purpose

Provisionamento de dados iniciais via migration Flyway contendo uma organização eclesial e o usuário administrador padrão para permitir a inicialização e o primeiro login no sistema ADM Eclesial.

## Requirements

### Requirement: Provisionamento Automático de Organização e Administrador Inicial
O sistema SHALL executar migration Flyway para garantir a existência de pelo menos uma organização eclesial ativa e uma conta de usuário com privilégios de `ADMINISTRADOR`.

#### Scenario: Primeiro Acesso com Credenciais Padrão
- **WHEN** o banco de dados for inicializado ou atualizado pelas migrations do Flyway
- **THEN** o sistema SHALL permitir autenticação com o login `admin` e a senha `admin123`.

#### Scenario: Vínculo Organizacional do Administrador
- **WHEN** o usuário `admin` realizar login com sucesso
- **THEN** o sistema SHALL conceder acesso à organização inicial cadastrada (`Paróquia Matriz - Sede`) com o perfil `ADMINISTRADOR`.
