# ADM Eclesial v16.10.6 — Correções de isolamento organizacional

Base: v16.10.5.3 (BUILD SUCCESS) + auditoria v16.10.6.

Correções desta entrega:
- autorização organizacional nos casos de uso auditados;
- validação por UUID e por ID interno de organização;
- listagens organizacionais filtradas;
- PESSOA visível por VINCULO_PESSOA_ORGANIZACAO_ECLESIAL;
- vínculos pessoa-organização e função protegidos;
- Financeiro, Repasses, Orçamento, Projetos e Recursos Vinculados protegidos;
- administração de usuários limitada às organizações administráveis;
- testes A→B e filtragem de listagem adicionados para Conta Financeira e Projeto;
- sem migration Flyway e sem alteração do modelo persistente.

Checkpoint obrigatório: `mvn clean test`.
