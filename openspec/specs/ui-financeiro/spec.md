# ui-financeiro Specification

## Purpose

Disponibiliza na interface web (Thymeleaf) a operação completa do módulo financeiro — contas, categorias, centros de custo, fornecedores, dizimistas, movimentações, transferências, contas a pagar, pagamentos, repasses e orçamento — para usuários autorizados, reutilizando as regras de negócio e o isolamento organizacional já existentes.

## Requirements

### Requirement: Operação de Contas Financeiras na interface web
O sistema SHALL permitir que usuários autorizados cadastrem, editem e listem contas financeiras pela interface web, validando que a organização esteja ativa e respeitando o escopo de organizações autorizadas.

#### Scenario: Cadastro de conta financeira com sucesso
- **WHEN** o usuário autorizado informa organização ativa, nome, tipo e submete o formulário de conta financeira
- **THEN** o sistema SHALL persistir a conta e retornar à listagem com mensagem de sucesso.

#### Scenario: Edição de conta financeira existente
- **WHEN** o usuário autorizado altera os dados de uma conta financeira existente
- **THEN** o sistema SHALL persistir as alterações e retornar à listagem com mensagem de sucesso.

#### Scenario: Organização inativa rejeitada
- **WHEN** o usuário tenta cadastrar uma conta financeira para uma organização inativa
- **THEN** o sistema SHALL rejeitar a operação e exibir a mensagem de erro de negócio no formulário.

### Requirement: Operação de Categorias Financeiras e Centros de Custo na interface web
O sistema SHALL permitir cadastrar e listar categorias financeiras, e cadastrar e listar centros de custo vinculados a uma organização autorizada.

#### Scenario: Cadastro de categoria financeira
- **WHEN** o usuário autorizado informa código, nome e tipo de uma categoria financeira
- **THEN** o sistema SHALL persistir a categoria e exibir a listagem atualizada.

#### Scenario: Cadastro de centro de custo
- **WHEN** o usuário autorizado informa organização autorizada, código e nome de um centro de custo
- **THEN** o sistema SHALL persistir o centro de custo e exibir a listagem atualizada.

#### Scenario: Isolamento organizacional na listagem de centros de custo
- **WHEN** o usuário autorizado acessa a listagem de centros de custo
- **THEN** o sistema SHALL exibir somente centros de custo de organizações autorizadas ao usuário.

### Requirement: Operação de Fornecedores na interface web
O sistema SHALL permitir cadastrar e listar fornecedores pela interface web, com tipo de pessoa, nome e dados de contato opcionais.

#### Scenario: Cadastro de fornecedor
- **WHEN** o usuário autorizado informa tipo de pessoa e nome de um fornecedor
- **THEN** o sistema SHALL persistir o fornecedor e exibir a listagem atualizada.

#### Scenario: Documento duplicado rejeitado
- **WHEN** o usuário cadastra um fornecedor com CPF/CNPJ já existente
- **THEN** o sistema SHALL rejeitar a operação e exibir a mensagem de erro de negócio.

### Requirement: Operação de Dizimistas na interface web
O sistema SHALL permitir cadastrar, editar e listar dizimistas, exigindo pessoa e organização ativas e período de vigência válido.

#### Scenario: Cadastro de dizimista
- **WHEN** o usuário autorizado informa pessoa ativa, organização ativa, data de início e submete o formulário
- **THEN** o sistema SHALL persistir o dizimista e exibir a listagem atualizada.

#### Scenario: Período inválido rejeitado
- **WHEN** o usuário informa data final anterior à data inicial de vigência
- **THEN** o sistema SHALL rejeitar a operação e exibir a mensagem de erro de negócio.

### Requirement: Registro de Movimentações Financeiras na interface web
O sistema SHALL permitir registrar e listar movimentações financeiras de entrada e saída pela interface web, exigindo conta financeira ativa, valor positivo e não permitindo o tipo TRANSFERENCIA no fluxo manual.

#### Scenario: Registro de movimentação de entrada
- **WHEN** o usuário autorizado informa conta ativa, tipo ENTRADA, valor positivo e data
- **THEN** o sistema SHALL persistir a movimentação e exibir a listagem atualizada.

#### Scenario: Transferência manual rejeitada
- **WHEN** o usuário tenta registrar manualmente uma movimentação do tipo TRANSFERENCIA
- **THEN** o sistema SHALL rejeitar a operação e orientar o uso do fluxo específico de transferência.

### Requirement: Realização de Transferências Financeiras na interface web
O sistema SHALL permitir realizar transferências pela interface web, exigindo duas contas distintas, ativas e da mesma organização.

#### Scenario: Transferência entre contas com sucesso
- **WHEN** o usuário autorizado seleciona conta de origem e destino distintas, ativas e da mesma organização, com valor e data válidos
- **THEN** o sistema SHALL registrar a transferência e as movimentações vinculadas e exibir confirmação de sucesso.

#### Scenario: Contas iguais rejeitadas
- **WHEN** o usuário seleciona a mesma conta como origem e destino
- **THEN** o sistema SHALL rejeitar a operação e exibir a mensagem de erro de negócio.

### Requirement: Operação de Contas a Pagar na interface web
O sistema SHALL permitir cadastrar e listar contas a pagar pela interface web, exigindo fornecedor ou obrigação de repasse, vencimento não anterior à emissão, e gerando automaticamente a primeira parcela.

#### Scenario: Cadastro de conta a pagar gera parcela
- **WHEN** o usuário autorizado informa organização ativa, fornecedor, descrição, datas e valor total
- **THEN** o sistema SHALL persistir a conta a pagar e criar a primeira parcela automaticamente, exibindo o resultado na listagem.

#### Scenario: Nem fornecedor nem obrigação informados
- **WHEN** o usuário não informa fornecedor nem obrigação de repasse, ou informa ambos
- **THEN** o sistema SHALL rejeitar a operação e exibir a mensagem de erro de negócio.

### Requirement: Registro de Pagamentos com Alocações na interface web
O sistema SHALL permitir registrar pagamentos pela interface web selecionando uma ou mais parcelas disponíveis, exigindo que a soma das alocações seja igual ao valor do pagamento, que a conta financeira e a conta a pagar pertençam à mesma organização e gerando a movimentação de saída.

#### Scenario: Pagamento com alocação integral
- **WHEN** o usuário autorizado seleciona parcelas disponíveis e informa alocações cuja soma é igual ao valor do pagamento, com conta financeira da mesma organização
- **THEN** o sistema SHALL registrar o pagamento, gerar a movimentação de saída e atualizar os status das parcelas e da conta a pagar.

#### Scenario: Soma das alocações diferente do valor
- **WHEN** o usuário informa alocações cuja soma difere do valor do pagamento
- **THEN** o sistema SHALL rejeitar a operação e exibir a mensagem de erro de negócio.

#### Scenario: Parcela duplicada no mesmo pagamento
- **WHEN** o usuário informa a mesma parcela mais de uma vez no pagamento
- **THEN** o sistema SHALL rejeitar a operação e exibir a mensagem de erro de negócio.

### Requirement: Operação de Regras e Obrigações de Repasse na interface web
O sistema SHALL permitir cadastrar e listar regras de repasse e gerar e listar obrigações de repasse pela interface web, preservando o snapshot de cálculo e criando a conta a pagar e parcela correspondentes.

#### Scenario: Cadastro de regra de repasse percentual
- **WHEN** o usuário autorizado informa origem e destino ativos e diferentes, periodicidade, base percentual e percentual entre 0 e 100
- **THEN** o sistema SHALL persistir a regra e exibir a listagem atualizada.

#### Scenario: Geração de obrigação de repasse
- **WHEN** o usuário autorizado gera uma obrigação a partir de uma regra com base e exclusões válidas
- **THEN** o sistema SHALL persistir a obrigação com o snapshot dos valores e criar a conta a pagar e a primeira parcela.

#### Scenario: Configuração inconsistente de regra rejeitada
- **WHEN** o usuário informa percentual em regra de valor fixo, ou valor fixo em regra percentual
- **THEN** o sistema SHALL rejeitar a operação e exibir a mensagem de erro de negócio.

### Requirement: Gestão de Orçamento na interface web
O sistema SHALL permitir criar orçamento por organização e ano, incluir itens enquanto o orçamento estiver PLANEJADO, aprovar com ao menos um item, encerrar apenas orçamento APROVADO e cancelar orçamento não ENCERRADO.

#### Scenario: Criação e aprovação de orçamento
- **WHEN** o usuário autorizado cria um orçamento, adiciona ao menos um item com categoria ativa e aciona aprovar
- **THEN** o sistema SHALL mudar o status para APROVADO e exibir a confirmação.

#### Scenario: Segundo orçamento para a mesma organização e ano
- **WHEN** o usuário tenta criar um orçamento para organização e ano já existentes
- **THEN** o sistema SHALL rejeitar a operação e exibir a mensagem de erro de negócio.

#### Scenario: Item em orçamento não planejado
- **WHEN** o usuário tenta incluir item em orçamento que não está PLANEJADO
- **THEN** o sistema SHALL rejeitar a operação e exibir a mensagem de erro de negócio.

#### Scenario: Cancelamento de orçamento encerrado
- **WHEN** o usuário tenta cancelar um orçamento ENCERRADO
- **THEN** o sistema SHALL rejeitar a operação e exibir a mensagem de erro de negócio.

### Requirement: Controle de acesso e feedback na interface financeira
O sistema SHALL restringir a interface financeira aos perfis ADMINISTRADOR e FINANCEIRO, manter CSRF habilitado e exibir mensagens de erro de negócio nos formulários sem perder os dados informados.

#### Scenario: Acesso negado a perfil não autorizado
- **WHEN** um usuário sem os perfis ADMINISTRADOR ou FINANCEIRO acessa uma rota da interface financeira
- **THEN** o sistema SHALL negar o acesso conforme a política de segurança vigente.

#### Scenario: Erro de negócio exibido no formulário
- **WHEN** uma operação financeira é rejeitada por violar uma regra de negócio
- **THEN** o sistema SHALL permanecer no formulário, exibir a mensagem de erro de negócio e preservar os valores informados.
