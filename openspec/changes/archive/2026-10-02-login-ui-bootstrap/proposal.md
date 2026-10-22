## Why

A tela de autenticação (`src/main/resources/templates/seguranca/login.html`) é a porta de entrada do sistema ADM Eclesial, porém ainda apresenta HTML básico sem estilização, destoando da identidade visual com Bootstrap 5.3 adotada nas demais telas e fragmentos do sistema.
A modernização visual da tela de login proporcionará uma experiência inicial acolhedora, profissional e responsiva, mantendo todos os parâmetros de autenticação e feedback do Spring Security inalterados.

## What Changes

- Aplicação de Bootstrap 5.3 e Bootstrap Icons na tela de login (`templates/seguranca/login.html`).
- Estruturação de layout centralizado com card moderno (`card shadow-sm border-0`), ícone institucional eclesiástico, tipografia elegante e foco em usabilidade.
- Estilização dos campos de formulário com `form-floating` ou `form-control` acompanhados de ícones (`bi bi-person`, `bi bi-key`).
- Estilização dos alertas de feedback do Spring Security:
  - Erro de autenticação (`param.error` -> `alert alert-danger d-flex align-items-center gap-2`).
  - Notificação de logout (`param.logout` -> `alert alert-info d-flex align-items-center gap-2`).
- Preservação integral do endpoint `/login`, método `POST`, parâmetros `username` e `password`, e do token CSRF do Spring Security.

## Capabilities

### New Capabilities
- `ui-login`: Modernização visual responsiva e padronização da tela de login utilizando Bootstrap 5.3 e componentes visuais institucionais.

### Modified Capabilities
<!-- Nenhuma especificação de regra de negócio existente foi alterada. -->

## Impact

- **Código:** Modificação em `templates/seguranca/login.html` e adição de estilos específicos de centralização em `static/css/app.css` se necessário.
- **Backend/Segurança:** Zero impacto na configuração de segurança (`SecurityConfig.java`) e no fluxo de autenticação.
