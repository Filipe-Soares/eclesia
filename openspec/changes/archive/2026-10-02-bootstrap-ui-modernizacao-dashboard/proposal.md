## Why

O sistema ADM Eclesial possui uma interface funcional e semântica em Thymeleaf, porém com estilos manuais básicos que limitam a usabilidade, a hierarquia visual das informações e a responsividade em diferentes dispositivos.
A adoção do Bootstrap 5.3 e do Bootstrap Icons no layout base compartilhado e nas visões de Dashboard elevará a experiência do usuário (UX/UI) e a clareza visual dos indicadores paroquiais, sem alterar quaisquer regras de negócio, permissões ou contratos de dados já estabelecidos.

## What Changes

- Integração do Bootstrap 5.3 (CSS/JS) e Bootstrap Icons via CDN / assets estáticos.
- Reestruturação do layout base compartilhado (`src/main/resources/templates/fragments/layout.html`):
  - Barra de cabeçalho moderna (`app-header` -> `navbar`) com identificação da paróquia e menu de usuário/logout estilizado.
  - Barra lateral (`app-sidebar`) com ícones para cada seção pastoral e administrativa, respeitando as regras de visibilidade do Spring Security (`sec:authorize`).
  - Padronização de mensagens de feedback (`feedback-success`, `feedback-error`, `feedback-warning`) e Empty States como componentes nativos de alerta e cards.
- Reformulação das 5 páginas de Dashboard (`src/main/resources/templates/dashboard/`):
  - `administrador.html`, `catequese.html`, `consulta.html`, `financeiro.html` e `secretaria.html`.
  - Apresentação de filtros (Organização e Período) em formulários alinhados e responsivos.
  - Exibição de cards de métricas com tipografia clara, sombras suaves (`shadow-sm`) e ícones ilustrativos.
  - Seções de alertas com destaque visual proporcional à criticidade (ex: contas a pagar vencidas, repasses curiais pendentes, turmas sem catequista).
- Criação de folha de estilo complementar para preservar a paleta de cores institucional e ajustes finos de layout.

## Capabilities

### New Capabilities
- `ui-bootstrap-dashboard`: Padronização visual com Bootstrap 5.3, Bootstrap Icons, layout mestre responsivo e telas de Dashboard modernizadas sem alteração de regras de negócio.

### Modified Capabilities
<!-- Nenhuma especificação de regra de negócio existente foi alterada. -->

## Impact

- **Código:** Modificação de `templates/fragments/layout.html`, templates em `templates/dashboard/*.html` e folha de estilos.
- **APIs/Backend:** Zero impacto nos Controllers, Services, DTOs ou Repositories.
- **Dependências:** Inclusão de referências para Bootstrap 5.3 e Bootstrap Icons.
