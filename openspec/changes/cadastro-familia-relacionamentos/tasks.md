## 1. Domínio e consistência de dados

- [x] 1.1 Tornar `RelacionamentoPessoa.dataInicio` obrigatório (`nullable=false`) e `RelacionamentoPessoaRequest.dataInicio` com `@NotNull`; verificar com teste de serviço que criar relacionamento sem data de início é rejeitado
- [x] 1.2 Confirmar que não é necessária nova migration e que o mapeamento continua compatível com `hibernate.ddl-auto=validate`; verificar executando o teste de contexto da aplicação

## 2. Application Services

- [x] 2.1 `FamiliaApplicationService`: adicionar `buscar`, `atualizar`, `inativar` e aplicar filtro por organização autorizada em `listar` (família sem membros apenas para ADMINISTRADOR); verificar com testes de serviço de edição e de visibilidade
- [x] 2.2 `MembroFamiliaApplicationService`: adicionar `listarPorFamilia`, `buscar`, `encerrar` e pré-checagem de associação ativa duplicada com erro de negócio; verificar com testes de serviço
- [x] 2.3 `RelacionamentoPessoaApplicationService`: adicionar `buscar`, `atualizar`, `remover`, `porPessoa` e implementar a criação automática do inverso sexo-aware e a remoção em par; verificar com testes cobrindo inversão simples, dependente de sexo, simétrica e remoção

## 3. API REST

- [x] 3.1 `FamiliaRestController`: adicionar `GET /{uuid}` e `PUT /{uuid}`; verificar com testes de controller
- [x] 3.2 `MembroFamiliaRestController`: adicionar `GET /{uuid}`, listagem por família e `POST /{uuid}/encerrar`; verificar com testes de controller
- [x] 3.3 `RelacionamentoPessoaRestController`: adicionar `GET /{uuid}`, `PUT /{uuid}`, `DELETE /{uuid}` e listagem por pessoa; verificar com testes de controller

## 4. Interface Web (Thymeleaf)

- [x] 4.1 Criar `FamiliaWebController` (`/familias`: lista, novo, criar, detalhe, editar, atualizar) reutilizando `FamiliaApplicationService`; verificar via teste de rendering das rotas
- [x] 4.2 Criar templates `familia/lista.html`, `familia/form.html`, `familia/detalhe.html` com gestão de membros no padrão dos demais módulos; verificar via teste de rendering
- [x] 4.3 Criar `RelacionamentoPessoaWebController` e template de relacionamentos (criação/remoção) vinculado à pessoa; verificar via teste de rendering
- [x] 4.4 Adicionar link "Famílias" ao grupo "Cadastros" em `templates/fragments/layout.html`; verificar no teste de rendering
- [x] 4.5 Adicionar seção de família e relacionamentos em `templates/pessoa/detalhe.html` (leitura com atalhos); verificar no teste de rendering

## 5. Segurança

- [x] 5.1 Confirmar/ajustar `SecurityConfig` para as rotas Web `/familias/**` e REST correspondentes conforme papéis (leitura 5 perfis; escrita ADMINISTRADOR/SECRETARIA); verificar com teste de autorização por perfil

## 6. Testes e validação final

- [x] 6.1 Cobrir Application Services de família, membros e relacionamentos com testes de unidade/integração (incluindo coerência recíproca e autorização); verificar com `mvn test` aprovado
- [x] 6.2 Executar `mvn clean test` (Docker ativo para Testcontainers) e confirmar BUILD SUCCESS com 0 falhas e 0 erros
- [x] 6.3 Executar `openspec validate cadastro-familia-relacionamentos --type change` e confirmar que a mudança é válida
