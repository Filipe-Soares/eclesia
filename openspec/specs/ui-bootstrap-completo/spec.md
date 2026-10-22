# ui-bootstrap-completo Specification

## Purpose

Padronização e modernização visual completa de todas as páginas operacionais, cadastros, relatórios e telas de erro do sistema ADM Eclesial com Bootstrap 5.3 e Bootstrap Icons.

## Requirements

### Requirement: Integração das Telas de Cadastro e Detalhes ao Layout Padrão
O sistema SHALL renderizar todas as páginas de listagem, formulários de criação/edição e telas de detalhe (Pessoas, Organizações, Usuários, Funções, Vínculos, Sacramentos, Matrimônio, Catequese, Projetos) dentro da estrutura com cabeçalho superior (`app-header`), menu lateral (`app-sidebar`) e componente de feedback/empty-state compativel com assinaturas flexíveis.

#### Scenario: Visualização de Listagens de Cadastro
- **WHEN** o usuário autorizado acessa a rota de listagem de qualquer cadastro ou módulo operacional (ex: `/pessoas`, `/organizacoes-eclesiais`, `/processos-matrimoniais`, `/sacramentos`, `/projetos`)
- **THEN** o sistema SHALL renderizar a tabela estilizada com classes do Bootstrap (`table table-hover table-striped`), cabeçalho de página com botão de novo registro e componente de empty-state (`fragments/layout :: empty-state`) sem erros de assinatura do Thymeleaf quando não houver registros.

#### Scenario: Visualização e Submissão de Formulários Operacionais
- **WHEN** o usuário acessa ou submete um formulário de cadastro
- **THEN** o sistema SHALL exibir os campos organizados no grid Bootstrap com classes `form-control` e `form-select`, destacando visualmente os erros de validação com mensagens de feedback.

### Requirement: Modernização dos Relatórios e Consultas
O sistema SHALL exibir os 8 relatórios institucionais com cards de filtros responsivos (`filter-card`), badges coloridos para status/situação, tabelas responsivas e controles de paginação nativos do Bootstrap.

#### Scenario: Consulta de Relatórios com Filtros
- **WHEN** o usuário submete os filtros em qualquer tela de relatório
- **THEN** o sistema SHALL exibir o total de registros, a paginação e os resultados formatados na tabela de relatório.

### Requirement: Modernização das Páginas de Erro e Exceção
O sistema SHALL exibir as páginas de erro (403, 404, 500 e erro genérico) com card centralizado, ícone representativo, mensagem explicativa e botões de ação para retorno ao sistema.

#### Scenario: Acesso a Recurso Inexistente ou Não Autorizado
- **WHEN** ocorrer um erro HTTP 403, 404 ou 500
- **THEN** o sistema SHALL apresentar a página de erro estilizada com Bootstrap, permitindo ao usuário retornar ao Dashboard ou à página inicial.
