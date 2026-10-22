## Why

Atualmente, diversas entidades centrais do sistema (Pessoas, Organizações Eclesiais, Funções Eclesiais, Vínculos, Projetos e Processos Matrimoniais) suportam apenas criação e visualização, impedindo a correção de dados digitados incorretamente pelos operadores e administradores.

## What Changes

- Implementar fluxo completo de edição e atualização (formulários preenchidos, rotas `GET /.../{uuid}/editar` e `POST /.../{uuid}/editar`, serviços de aplicação e validações de unicidade/integridade) para:
  - **Pessoas** (`/pessoas/{uuid}/editar`)
  - **Organizações Eclesiais** (`/organizacoes-eclesiais/{uuid}/editar`)
  - **Funções Eclesiais** (`/funcoes-eclesiais/{uuid}/editar`)
  - **Vínculos Pessoa × Organização** (`/vinculos-pessoa-organizacao/{uuid}/editar`)
  - **Projetos e Obras** (`/projetos/{uuid}/editar`)
  - **Processos Matrimoniais** (`/processos-matrimoniais/{uuid}/editar`)
- Adicionar botões de ação "Editar" nas telas de listagem e de detalhes dessas entidades.
- Reutilizar e adequar os templates de formulário existentes para suportar tanto o modo de criação quanto o modo de edição.

## Capabilities

### New Capabilities
- `edicao-cadastros`: Capacidade de editar e atualizar registros existentes de entidades institucionais, pessoais e pastorais.

### Modified Capabilities
<!-- None -->

## Impact

- Controladores Web (`PessoaWebController`, `OrganizacaoEclesialWebController`, `FuncaoEclesialWebController`, etc.).
- Serviços de Aplicação (`PessoaApplicationService`, `OrganizacaoEclesialApplicationService`, etc.).
- Templates Thymeleaf de formulário (`form.html`), listagem (`lista.html`) e detalhes (`detalhe.html`).
