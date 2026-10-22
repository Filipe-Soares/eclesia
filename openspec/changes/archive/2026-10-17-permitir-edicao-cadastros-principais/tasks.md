## 1. Implementação do Fluxo de Edição de Pessoas e Organizações

- [x] 1.1 Implementar método `atualizar` em `PessoaApplicationService` e endpoints `GET/POST /pessoas/{uuid}/editar` em `PessoaWebController`, com botões de edição em `templates/pessoa/detalhe.html`, `templates/pessoa/lista.html` e suporte no formulário `templates/pessoa/form.html`
- [x] 1.2 Implementar método `atualizar` em `OrganizacaoEclesialApplicationService` e endpoints `GET/POST /organizacoes-eclesiais/{uuid}/editar` em `OrganizacaoEclesialWebController`, com botões de edição em `templates/organizacao/detalhe.html`, `templates/organizacao/lista.html` e suporte no formulário `templates/organizacao/form.html`

## 2. Implementação do Fluxo de Edição de Funções, Vínculos, Projetos e Processos Matrimoniais

- [x] 2.1 Implementar edição em `FuncaoEclesialApplicationService`/`FuncaoEclesialWebController` e `VinculoPessoaOrganizacaoApplicationService`/`VinculoPessoaOrganizacaoWebController` com suporte em seus respectivos formulários e telas
- [x] 2.2 Implementar edição em `ProjetoApplicationService`/`ProjetoWebController` e `ProcessoMatrimonialApplicationService`/`ProcessoMatrimonialWebController` com suporte em seus respectivos formulários e telas

## 3. Validação e Testes Automatizados

- [x] 3.1 Executar a suíte completa de testes (`mvn test`) garantindo 100% de aprovação em todos os novos fluxos de atualização e testes existentes
