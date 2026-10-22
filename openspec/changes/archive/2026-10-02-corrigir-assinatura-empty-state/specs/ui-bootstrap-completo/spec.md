## MODIFIED Requirements

### Requirement: Integração das Telas de Cadastro e Detalhes ao Layout Padrão
O sistema SHALL renderizar todas as páginas de listagem, formulários de criação/edição e telas de detalhe (Pessoas, Organizações, Usuários, Funções, Vínculos, Sacramentos, Matrimônio, Catequese, Projetos) dentro da estrutura com cabeçalho superior (`app-header`), menu lateral (`app-sidebar`) e componente de feedback/empty-state compativel com assinaturas flexíveis.

#### Scenario: Visualização de Listagens de Cadastro
- **WHEN** o usuário autorizado acessa a rota de listagem de qualquer cadastro ou módulo operacional (ex: `/pessoas`, `/organizacoes-eclesiais`, `/processos-matrimoniais`, `/sacramentos`, `/projetos`)
- **THEN** o sistema SHALL renderizar a tabela estilizada com classes do Bootstrap (`table table-hover table-striped`), cabeçalho de página com botão de novo registro e componente de empty-state (`fragments/layout :: empty-state`) sem erros de assinatura do Thymeleaf quando não houver registros.

#### Scenario: Visualização e Submissão de Formulários Operacionais
- **WHEN** o usuário acessa ou submete um formulário de cadastro
- **THEN** o sistema SHALL exibir os campos organizados no grid Bootstrap com classes `form-control` e `form-select`, destacando visualmente os erros de validação com mensagens de feedback.
