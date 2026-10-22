## Context

O frontend do ADM Eclesial utiliza Thymeleaf com layout fragmentado (`templates/fragments/layout.html`) e regras de autorização com `thymeleaf-extras-springsecurity6`. A estilização anterior era mantida exclusivamente em `static/css/app.css`. Veja `proposal.md` para a motivação da modernização.

## Goals / Non-Goals

**Goals:**
- Integrar Bootstrap 5.3.3 e Bootstrap Icons 1.11.x via CDN de alta disponibilidade (com assets locais ou fallback limpo).
- Redesenhar os fragmentos de layout (`header`, `sidebar`, `feedback`, `empty-state`) para utilizar classes padrão do Bootstrap com tipografia e espaçamento aprimorados.
- Reformular as 5 telas de Dashboard (`administrador.html`, `catequese.html`, `consulta.html`, `financeiro.html`, `secretaria.html`) para utilizar cards modernos, badges e grid responsivo.
- Preservar rigorosamente todas as diretivas Thymeleaf (`th:*`), links de formulário e verificações de segurança (`sec:authorize`).

**Non-Goals:**
- Não alterar regras de negócio, serviços Java, controllers, DTOs ou entidades JPA.
- Não reescrever todos os formulários e tabelas dos mais de 20 módulos do sistema nesta primeira fase (foco no layout mestre + Dashboards).
- Não introduzir dependências pesadas de JavaScript/SPA (como React ou Angular) ou frameworks que exijam build frontend Node.js complexo.

## Decisions

1. **Bootstrap 5.3 + Bootstrap Icons via CDN com CSS Customizado Complementar:**
   - *Decisão:* Carregar o Bootstrap 5.3.3 e Bootstrap Icons 1.11 via CDN oficial no `<head>` de `layout.html` (e páginas que carregam o layout) e manter um arquivo `custom-theme.css` (ou atualizar `app.css`) para cores e ajustes finos da identidade eclesial.
   - *Alternativas consideradas:* WebJars Maven (rejeitado a pedido do usuário em prol de estáticos/CDN) ou rebuild manual com SASS/Node (desnecessário para o MVP).

2. **Estrutura de Layout Base (`layout.html`):**
   - *Decisão:* Adotar estrutura com barra superior fixa (`navbar navbar-dark bg-primary-eclesial`) e barra lateral responsiva (`sidebar` colapsável em telas menores com ícones do Bootstrap Icons para cada menu).
   - *Alternativas consideradas:* Menu apenas no topo ou sidebar estilo Accordion rígido.

3. **Cards e Métricas do Dashboard:**
   - *Decisão:* Utilizar cards Bootstrap (`card border-0 shadow-sm`) com borda colorida suave na lateral ou ícone de destaque, facilitando a leitura rápida dos totais (entradas, saídas, turmas ativas, projetos).
   - *Alternativas consideradas:* Tabelas simples ou listas sem agrupamento.

## Risks / Trade-offs

- **[Risco] Conflito de estilos herdados de `app.css`:**
  - *Mitigação:* Isolar ou refatorar as classes de `app.css` para que funcionem em harmonia com as classes utilitárias do Bootstrap sem sobrescrever indevidamente reset e grid.
- **[Risco] Quebra visual em telas muito pequenas (mobile):**
  - *Mitigação:* Usar os breakpoints padrão do Bootstrap (`col-12 col-md-6 col-lg-3`) e layout de tabela responsiva (`table-responsive`) nos módulos.
