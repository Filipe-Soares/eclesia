## 1. Implementação da Migration Flyway

- [x] 1.1 Criar o arquivo `src/main/resources/db/migration/V9__seed_inicial_admin.sql` com inserts idempotentes para organização, pessoa, usuário `admin` (senha `admin123`), perfil e permissão organizacional
- [x] 1.2 Verificar a sintaxe SQL e conformidade com o schema `adm_eclesial` do PostgreSQL

## 2. Validação e Testes

- [x] 2.1 Executar os testes automatizados do Spring Boot (`mvn test`) para verificar a execução da migration pelo Flyway e a integridade de todas as suites de teste
- [x] 2.2 Confirmar que a autenticação com `admin` e `admin123` é aceita com sucesso
