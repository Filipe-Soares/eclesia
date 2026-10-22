## 1. Navegação e Cadastros Simples

- [x] 1.1 Criar controller web e templates (`lista.html`/`form.html`) para **Contas Financeiras** em `interfaces/web/financeiro/` e `templates/financeiro/contas/`, reutilizando `ContaFinanceiraApplicationService`; verificar criando e editando uma conta e conferindo a listagem.
- [x] 1.2 Criar controller web e templates para **Categorias Financeiras**; verificar cadastrando uma categoria e conferindo a listagem atualizada.
- [x] 1.3 Criar controller web e templates para **Centros de Custo**; verificar cadastrando um centro de custo e conferindo que a listagem respeita o isolamento organizacional.
- [x] 1.4 Criar controller web e templates para **Fornecedores**; verificar cadastrando um fornecedor e que documento duplicado exibe erro de negócio.
- [x] 1.5 Criar controller web e templates para **Dizimistas** (criar/editar/listar); verificar cadastro válido e rejeição de período inválido.
- [x] 1.6 Atualizar `templates/financeiro/index.html` com links de navegação para os recursos e ajustar a sidebar em `templates/fragments/layout.html`; verificar que os links aparecem apenas para ADMINISTRADOR/FINANCEIRO.

## 2. Fluxos Financeiros Compostos

- [x] 2.1 Criar controller web e templates para **Movimentações Financeiras** (registrar/listar); verificar registro de ENTRADA e rejeição de TRANSFERENCIA manual.
- [x] 2.2 Criar controller web e templates para **Transferências Financeiras**; verificar transferência entre contas distintas da mesma organização e rejeição de contas iguais.
- [x] 2.3 Criar controller web e templates para **Contas a Pagar** (criar/listar); verificar que a primeira parcela é gerada e que a ausência/duplicidade de fornecedor+obrigação é rejeitada.
- [x] 2.4 Criar controller web e templates para **Pagamentos** com seleção de parcelas e alocações; verificar pagamento com alocação integral, rejeição de soma divergente e de parcela duplicada.

## 3. Repasses e Orçamento

- [x] 3.1 Criar controller web e templates para **Regras de Repasse**; verificar regra percentual válida e rejeição de configuração inconsistente (percentual x valor fixo).
- [x] 3.2 Criar controller web e templates para **Obrigações de Repasse** (gerar/listar); verificar geração com snapshot de cálculo e criação da conta a pagar/parcela.
- [x] 3.3 Criar controller web e templates para **Orçamento** (criar, itens, listar) e ações de status (aprovar/encerrar/cancelar) com tela de detalhe; verificar ciclo completo, rejeição de segundo orçamento para mesma organização/ano e de cancelamento de orçamento encerrado.

## 4. Validação Final

- [x] 4.1 Adicionar/ajustar testes de controller web (MockMvc) cobrindo cadastro/edição com sucesso e ao menos um erro de negócio por recurso; verificar execução bem-sucedida dos novos testes.
- [x] 4.2 Executar `mvn clean test` e confirmar BUILD SUCCESS sem regressões.
- [x] 4.3 Conferir manualmente a matriz de permissões (§19) e o CSRF nas rotas financeiras; verificar acesso negado para perfis não autorizados.
