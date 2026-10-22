## Purpose

Permite que a secretaria mantenha famílias e relacionamentos entre pessoas — criação, edição, composição de membros e vínculos familiares — com regras de período, coerência recíproca e autorização por organização, acessível tanto pela interface Web quanto pela API REST.

## ADDED Requirements

### Requirement: Cadastro de família
O sistema SHALL permitir que usuários autorizados criem, consultem, editem e inativem famílias, com nome obrigatório e estado ativo/inativo.

#### Scenario: Criação de família
- **WHEN** um usuário autorizado informa o nome e submete o formulário de nova família
- **THEN** o sistema SHALL persistir a família e exibir a tela de detalhe com mensagem de sucesso

#### Scenario: Edição de família
- **WHEN** o usuário altera o nome ou o estado ativo de uma família existente
- **THEN** o sistema SHALL persistir a alteração e exibir mensagem de sucesso

#### Scenario: Nome obrigatório
- **WHEN** o usuário submete o formulário sem nome
- **THEN** o sistema SHALL rejeitar a operação e exibir mensagem de validação

### Requirement: Associação de membros de família
O sistema SHALL permitir associar pessoas a uma família por um período, com um tipo de membro (RESPONSAVEL, CONJUGE, FILHO, OUTRO), exigindo família e pessoa ativas, e SHALL permitir encerrar a associação preservando o histórico.

#### Scenario: Adicionar membro
- **WHEN** o usuário associa uma pessoa ativa a uma família ativa com tipo e data de início válidos
- **THEN** o sistema SHALL registrar o vínculo e exibi-lo no detalhe da família

#### Scenario: Encerrar associação
- **WHEN** o usuário informa a data de fim de um membro
- **THEN** o sistema SHALL encerrar a associação sem removê-la do histórico

#### Scenario: Família ou pessoa inativa
- **WHEN** o usuário tenta associar uma pessoa ou família inativa
- **THEN** o sistema SHALL rejeitar a operação com mensagem explicativa

#### Scenario: Associação ativa duplicada
- **WHEN** o usuário tenta associar uma pessoa que já possui associação ativa na mesma família
- **THEN** o sistema SHALL rejeitar a operação com mensagem amigável, sem erro de integridade

### Requirement: Visibilidade de famílias por organização autorizada
O sistema SHALL restringir a listagem e a consulta de famílias às organizações autorizadas do usuário, considerando as organizações das pessoas associadas como membros; família sem membros SHALL ser acessível apenas a ADMINISTRADOR.

#### Scenario: Listagem restrita
- **WHEN** um usuário não administrador acessa a lista de famílias
- **THEN** o sistema SHALL exibir apenas famílias com ao menos um membro visível em suas organizações autorizadas

#### Scenario: Família sem membros
- **WHEN** um usuário não administrador tenta acessar uma família sem membros
- **THEN** o sistema SHALL negar o acesso

### Requirement: Cadastro de relacionamentos entre pessoas
O sistema SHALL permitir registrar, consultar, atualizar e remover relacionamentos entre duas pessoas distintas, com tipo (PAI, MAE, FILHO, FILHA, CONJUGE, IRMAO, IRMA, AVO, AVA, NETO, NETA, OUTRO), data de início obrigatória e período válido, exigindo que ambas as pessoas existam, estejam ativas e sejam diferentes.

#### Scenario: Criar relacionamento
- **WHEN** o usuário registra um relacionamento entre duas pessoas ativas e distintas com data de início válida
- **THEN** o sistema SHALL persistir o relacionamento e registrar automaticamente o relacionamento inverso correspondente

#### Scenario: Pessoa inativa ou igual
- **WHEN** o usuário tenta relacionar a mesma pessoa ou uma pessoa inativa
- **THEN** o sistema SHALL rejeitar a operação com mensagem explicativa

#### Scenario: Data de início obrigatória
- **WHEN** o usuário submete um relacionamento sem data de início
- **THEN** o sistema SHALL rejeitar a operação

#### Scenario: Período inválido
- **WHEN** a data de fim é anterior à data de início
- **THEN** o sistema SHALL rejeitar a operação

### Requirement: Coerência recíproca de relacionamentos
O sistema SHALL manter a coerência recíproca dos relacionamentos na aplicação (sem trigger de banco), criando o relacionamento inverso conforme o tipo e o sexo da pessoa de destino, e SHALL evitar a duplicação do par.

#### Scenario: Inversão simples
- **WHEN** é registrado o relacionamento "PAI" de A para B
- **THEN** o sistema SHALL registrar o inverso "FILHO" de B para A

#### Scenario: Inversão dependente de sexo
- **WHEN** o tipo inverso depende do sexo (FILHO/FILHA, IRMAO/IRMA, NETO/NETA, AVO/AVA)
- **THEN** o sistema SHALL usar o sexo da pessoa de destino para escolher o tipo inverso

#### Scenario: Relação simétrica
- **WHEN** é registrado o relacionamento "CONJUGE" entre A e B
- **THEN** o sistema SHALL registrar o inverso "CONJUGE" de B para A

#### Scenario: Remoção em par
- **WHEN** o usuário remove um relacionamento
- **THEN** o sistema SHALL remover também o respectivo relacionamento inverso

### Requirement: Exposição em Web e API
O sistema SHALL oferecer as operações de família, membros e relacionamentos tanto na interface Web (Thymeleaf) quanto na API REST, aplicando as mesmas regras de negócio e de autorização.

#### Scenario: Paridade entre Web e REST
- **WHEN** uma operação de família, membro ou relacionamento é executada pela API REST
- **THEN** o sistema SHALL aplicar as mesmas validações e regras de autorização da interface Web

#### Scenario: Seção na ficha da Pessoa
- **WHEN** o usuário abre a ficha de uma pessoa
- **THEN** o sistema SHALL exibir uma seção com as famílias e relacionamentos da pessoa, respeitando a autorização por organização
