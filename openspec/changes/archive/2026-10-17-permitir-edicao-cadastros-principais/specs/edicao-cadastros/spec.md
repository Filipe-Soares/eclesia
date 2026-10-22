## Purpose

Permite que usuários autorizados editem e atualizem as informações de registros já cadastrados no sistema para retificação de dados.

## ADDED Requirements

### Requirement: Edição de Dados Pessoais
O sistema SHALL permitir que usuários autorizados atualizem os dados cadastrais de uma Pessoa existente (`/pessoas/{uuid}/editar`), validando regras de unicidade de CPF (permitindo manter o mesmo CPF do próprio registro) e persistindo as alterações.

#### Scenario: Atualização de dados cadastrais de Pessoa com sucesso
- **WHEN** o usuário altera os campos de uma pessoa e submete o formulário de edição
- **THEN** o sistema SHALL salvar as alterações no banco de dados e redirecionar para a tela de detalhes com mensagem de sucesso.

### Requirement: Edição de Organizações Eclesiais
O sistema SHALL permitir que administradores atualizem os dados cadastrais de uma Organização Eclesial existente (`/organizacoes-eclesiais/{uuid}/editar`), persistindo as alterações.

#### Scenario: Atualização de Organização Eclesial com sucesso
- **WHEN** o administrador altera os dados de uma organização e submete o formulário de edição
- **THEN** o sistema SHALL salvar as alterações e redirecionar para a tela de detalhes da organização com mensagem de sucesso.

### Requirement: Edição de Funções Eclesiais e Vínculos
O sistema SHALL permitir a edição dos dados de Funções Eclesiais (`/funcoes-eclesiais/{uuid}/editar`) e de Vínculos de Pessoas com Organizações (`/vinculos-pessoa-organizacao/{uuid}/editar`).

#### Scenario: Atualização de Função ou Vínculo com sucesso
- **WHEN** o usuário submete o formulário de edição de uma função ou vínculo existente
- **THEN** o sistema SHALL atualizar os dados e redirecionar para a listagem ou detalhe correspondente com mensagem de confirmação.
