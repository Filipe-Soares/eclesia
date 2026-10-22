# Checklist de Release — v16.10.7

## Código congelado
- [x] Baseado na v16.10.6.1
- [x] Mudanças pós-baseline incorporadas (edição de cadastros principais e modernização da UI financeira)
- [x] Sem nova migration
- [x] Sem nova tabela/coluna
- [x] Sem alteração de regras de negócio

## Validação registrada
- [x] 136 testes
- [x] 0 falhas
- [x] 0 erros
- [x] BUILD SUCCESS

## Antes de produção
- [ ] Configurar PostgreSQL do ambiente
- [ ] Definir credenciais por configuração externa
- [ ] Definir senha inicial segura do administrador quando aplicável
- [ ] Executar `mvn clean test`
- [ ] Validar migrations Flyway no banco de destino
- [ ] Validar organizações atribuídas aos usuários
- [ ] Validar perfis de acesso
- [ ] Fazer backup e definir rotina operacional de backup
- [ ] Validar logs e parâmetros específicos do ambiente

## Regra de evolução
Novas funcionalidades devem iniciar uma versão pós-MVP baseada nesta release.
