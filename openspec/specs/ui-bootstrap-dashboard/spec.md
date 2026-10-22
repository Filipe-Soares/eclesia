# ui-bootstrap-dashboard Specification

## Purpose

Padronização e modernização da camada de apresentação visual do sistema ADM Eclesial utilizando Bootstrap 5.3 e Bootstrap Icons, com foco na estrutura mestre e nas páginas de Dashboard, mantendo todas as regras de negócio e controle de acesso inalterados.

## Requirements

### Requirement: Layout Base Responsivo com Bootstrap
O sistema SHALL fornecer um layout base consistente em `templates/fragments/layout.html` utilizando o sistema de grid e componentes do Bootstrap 5.3, contendo cabeçalho institucional, menu lateral categorizado por contexto pastoral/administrativo, área de feedback para mensagens do sistema e componente de estado vazio.

#### Scenario: Exibição do Cabeçalho e Identificação do Usuário
- **WHEN** o usuário autenticado acessa qualquer página do sistema
- **THEN** o sistema SHALL renderizar a barra superior com o título da aplicação, nome do usuário logado e opção de logout estilizada com botão Bootstrap.

#### Scenario: Navegação Lateral com Permissões
- **WHEN** o menu lateral é renderizado
- **THEN** o sistema SHALL exibir os links e agrupamentos correspondentes estritamente aos papéis de acesso do usuário logado (`sec:authorize`), acompanhados de ícones representativos do Bootstrap Icons.

#### Scenario: Exibição de Mensagens de Feedback
- **WHEN** uma operação for concluída com mensagem de sucesso, erro ou alerta
- **THEN** o sistema SHALL exibir o alerta formatado nas classes correspondentes do Bootstrap (`alert-success`, `alert-danger`, `alert-warning`).

### Requirement: Modernização Visual dos Dashboards
O sistema SHALL renderizar as visões de Dashboard (`administrador.html`, `catequese.html`, `consulta.html`, `financeiro.html` e `secretaria.html`) com cards de métricas em grid responsivo, formulário de filtro de período/organização estilizado e seções de alertas com indicadores de criticidade.

#### Scenario: Visualização de Métricas no Dashboard
- **WHEN** o usuário autorizado acessa o dashboard de seu perfil
- **THEN** o sistema SHALL apresentar os indicadores de contagem e valores financeiros formatados dentro de cards com sombras suaves (`card`, `shadow-sm`) e tipografia legível.

#### Scenario: Filtragem por Organização e Período
- **WHEN** o usuário submete os filtros de organização ou data no dashboard
- **THEN** o sistema SHALL manter os parâmetros informados e recarregar as métricas nos componentes visuais sem quebrar o layout.

#### Scenario: Alertas de Gestão e Vencimento
- **WHEN** houver pendências (contas a pagar vencidas, repasses pendentes ou turmas sem catequista)
- **THEN** o sistema SHALL destacar essas pendências em blocos de alerta visual com links diretos para os relatórios detalhados correspondentes.
