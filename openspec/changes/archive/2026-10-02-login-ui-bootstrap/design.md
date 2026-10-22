## Context

A tela de login em `src/main/resources/templates/seguranca/login.html` foi concebida inicialmente como um formulário HTML cru. Com a adoção recente do Bootstrap 5.3 e do Bootstrap Icons no layout principal, a tela de login precisa ser harmonizada visualmente. Veja `proposal.md` para motivação.

## Goals / Non-Goals

**Goals:**
- Prover um design moderno de tela cheia (`min-vh-100 d-flex align-items-center justify-content-center`) com fundo suave e card elevado.
- Incluir referências a Bootstrap 5.3 CSS/JS, Bootstrap Icons e `app.css`.
- Integrar ícones nos inputs para melhorar a legibilidade e usabilidade.
- Estilizar alertas de erro (`param.error`) e encerramento de sessão (`param.logout`).

**Non-Goals:**
- Não alterar endpoints, parâmetros (`username`, `password`), filtros ou provedores de autenticação (`SecurityConfig.java` e `AdmEclesialUserDetailsService.java`).
- Não adicionar fluxos complexos como recuperação de senha ou autenticação multifator nesta etapa.

## Decisions

1. **Card de Autenticação Centralizado:**
   - *Decisão:* Utilizar layout `d-flex align-items-center justify-content-center min-vh-100` com um card de largura máxima ~420px, sombra suave (`shadow-sm`) e cabeçalho visual com ícone de catedral (`bi bi-bank2`) e título "ADM Eclesial".
   - *Alternativas consideradas:* Layout dividido (split-screen com imagem lateral). Optou-se pelo card centralizado pela simplicidade, leveza e foco imediato no login.

2. **Aproveitamento dos Assets Comuns:**
   - *Decisão:* Importar Bootstrap 5.3 e Bootstrap Icons diretamente no `<head>` de `login.html` ou utilizar o fragmento de assets do layout para consistência com o restante do sistema.

## Risks / Trade-offs

- **[Risco] Esquecimento do token CSRF em formulários POST:**
  - *Mitigação:* Usar `th:action="@{/login}"`, permitindo que o Thymeleaf inclua automaticamente o campo oculto `_csrf`.
