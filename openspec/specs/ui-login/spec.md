# ui-login Specification

## Purpose

Padronização e modernização visual da tela de login do sistema ADM Eclesial com Bootstrap 5.3 e Bootstrap Icons, garantindo responsividade, centralização de tela e clareza nas mensagens de autenticação e logout.

## Requirements

### Requirement: Layout Centralizado e Responsivo de Autenticação
O sistema SHALL renderizar a página de login em `templates/seguranca/login.html` com layout centralizado vertical e horizontalmente, utilizando componentes de card e grid do Bootstrap 5.3, com identidade visual compatível com o tema eclesiástico.

#### Scenario: Visualização Inicial da Tela de Login
- **WHEN** o usuário não autenticado acessa a rota `/login`
- **THEN** o sistema SHALL exibir o card de login centralizado contendo o logotipo/ícone institucional, título da aplicação, campos de login e senha com rótulos legíveis e botão destacado de submissão.

#### Scenario: Submissão de Credenciais Inválidas
- **WHEN** a autenticação falhar e o Spring Security redirecionar para `/login?error`
- **THEN** o sistema SHALL exibir a mensagem de erro "Login ou senha inválidos." dentro de um componente de alerta com estilo de erro do Bootstrap (`alert-danger`).

#### Scenario: Notificação de Logout Realizado
- **WHEN** o usuário realizar logout com sucesso e for redirecionado para `/login?logout`
- **THEN** o sistema SHALL exibir a notificação "Sessão encerrada." dentro de um componente de alerta informativo do Bootstrap (`alert-info`).
