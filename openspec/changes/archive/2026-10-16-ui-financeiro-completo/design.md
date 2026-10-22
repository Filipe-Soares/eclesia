## Context

Ver `proposal.md` para motivação e escopo. O módulo financeiro já possui Application Services completos com regras de negócio, validação de isolamento organizacional (`SegurancaOrganizacaoService`) e APIs REST. A camada web atual (`FinanceiroWebController`) expõe apenas leitura. O padrão de UI do projeto é o par de templates `lista.html`/`form.html` (+ `detalhe.html` quando aplicável), com `fragments/layout.html` (sidebar, feedback, empty-state) e proteção via `sec:authorize`.

## Goals / Non-Goals

**Goals:**
- Expor na interface web as operações financeiras reutilizando os Application Services existentes, sem duplicar regras.
- Seguir o padrão visual e de navegação já usado nas demais áreas (cadastros, pastoral, projetos).
- Tratar erros de negócio exibindo a mensagem no formulário, preservando os dados informados.
- Expor ações de status do orçamento (aprovar, encerrar, cancelar) e o fluxo de alocação de pagamento.

**Non-Goals:**
- Não alterar schema, migrations, entidades ou regras de negócio.
- Não criar/alterar Application Services ou APIs REST.
- Não implementar funcionalidades pós-MVP (conciliação bancária, contabilidade, motor automático de repasses, etc.).

## Decisions

1. **Decisão 1: Reutilizar `FinanceiroWebController` como hub, com controllers por recurso.**
   - Manter `/financeiro` como visão geral e adicionar controllers web por recurso em `interfaces/web/financeiro/` (contas, categorias, centros de custo, fornecedores, dizimistas, movimentações, transferências, contas a pagar, pagamentos, regras de repasse, obrigações de repasse, orçamento), espelhando a organização de `interfaces/rest/financeiro/`.
   - Alternativa considerada: um único controller monolítico — descartada por dificultar manutenção e mapeamento de rotas.

2. **Decisão 2: Estrutura de templates por recurso.**
   - `templates/financeiro/<recurso>/lista.html` e `form.html`; orçamento também com `detalhe.html` para itens e ações de status; pagamento com formulário de alocação de parcelas.
   - Ajustar `financeiro/index.html` para links de navegação para cada recurso.

3. **Decisão 3: Reutilizar regras sem reimplementar validações.**
   - Controllers apenas orquestram: montam o request, chamam o service e tratam exceções de negócio. Todas as validações (organização ativa, valor positivo, alocações, status de orçamento) permanecem nos services.

4. **Decisão 4: Exibição de erros de negócio.**
   - Capturar exceções de negócio no controller e retornar o próprio template com `businessError`, seguindo o padrão das telas existentes (ex.: `SacramentoWebController`).

5. **Decisão 5: Autorização.**
   - Proteger rotas com perfil ADMINISTRADOR/FINANCEIRO coerente com a matriz de permissões do manual (§19) e com o menu atual, mantendo CSRF habilitado. O isolamento organizacional permanece garantido pelos services.

## Risks / Trade-offs

- [Risco] Divergência entre a UI e as restrições do backend (ex.: tipos de enum, combinações válidas de repasse). → [Mitigação] Popular selects com os enums de domínio e delegar a validação final aos services, exibindo o erro retornado.
- [Risco] Formulário de pagamento com múltiplas alocações tornar-se complexo. → [Mitigação] Iniciar com seleção de parcela(s) disponíveis e validação de soma no backend; evoluir a UX apenas se necessário.
- [Risco] Rotas financeiras expostas a perfil indevido. → [Mitigação] Reforçar `sec:authorize` no menu e aplicar proteção por perfil nas rotas, sem confiar apenas na ocultação do menu.

## Migration Plan

1. Implementar controllers e templates por recurso, começando por cadastros simples (contas, categorias, centros de custo, fornecedores, dizimistas).
2. Avançar para fluxos compostos (movimentações, transferências, contas a pagar, pagamentos).
3. Finalizar com repasses e orçamento.
4. Validar via `mvn clean test` (incluindo testes de controller web) e conferir a matriz de permissões.
5. Rollback: remover as novas rotas/templates; nenhuma migração ou alteração de dados é necessária.
