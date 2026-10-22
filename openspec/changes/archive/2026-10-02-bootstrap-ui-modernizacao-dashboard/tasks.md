## 1. Configuração de Assets e Estilos Base

- [x] 1.1 Incluir referências do Bootstrap 5.3.3 e Bootstrap Icons 1.11 no layout base (`src/main/resources/templates/fragments/layout.html`) e verificar carregamento visual correto dos estilos e ícones
- [x] 1.2 Criar folha de estilo customizada ou refatorar `src/main/resources/static/css/app.css` com variáveis de cores institucionais e ajustes para coexistência com o Bootstrap, verificando se não há quebra de layout existente

## 2. Reestruturação do Layout Base Compartilhado

- [x] 2.1 Refatorar fragmento `header` em `layout.html` utilizando classes `navbar` e componentes Bootstrap, preservando o formulário de logout e `sec:authentication="name"`
- [x] 2.2 Refatorar fragmento `sidebar` em `layout.html` com navegação estilizada, ícones do Bootstrap Icons para cada item e preservando as diretivas `sec:authorize`
- [x] 2.3 Refatorar fragmentos `feedback` e `empty-state` em `layout.html` utilizando componentes `alert` e `card` do Bootstrap, verificando a exibição de mensagens de sucesso, erro e estado vazio

## 3. Modernização das Telas de Dashboard

- [x] 3.1 Modernizar a tela `dashboard/administrador.html` aplicando cards de métricas (`card shadow-sm`), grid responsivo, formulário de filtro com `form-select`/`form-control` e alertas visuais de pendências
- [x] 3.2 Modernizar a tela `dashboard/secretaria.html` com cards de visão geral e atalhos rápidos estilizados com Bootstrap
- [x] 3.3 Modernizar a tela `dashboard/catequese.html` com métricas pastorais, turmas e inscritos em layout de cards padronizados
- [x] 3.4 Modernizar a tela `dashboard/financeiro.html` com cards destacados de entradas, saídas, resultado e alertas de contas/repasses vencidos
- [x] 3.5 Modernizar a tela `dashboard/consulta.html` com visual limpo e navegação aos relatórios

## 4. Validação e Testes

- [x] 4.1 Executar a suíte de testes existente do Spring Boot (`mvn test`) para garantir que nenhuma regra de negócio ou controller foi impactado
- [x] 4.2 Validar responsividade e renderização das páginas no navegador em resoluções desktop e mobile
