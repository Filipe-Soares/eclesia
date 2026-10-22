## Purpose

Padroniza a renderização condicional de estados vazios (empty-state) nas páginas de listagem com Thymeleaf, evitando que avisos de ausência de registros sejam exibidos simultaneamente com tabelas populadas.

## ADDED Requirements

### Requirement: Renderização Condicional Exclusiva de Empty State
O sistema SHALL exibir o componente de empty-state exclusivamente quando a coleção de dados do modelo estiver vazia, e SHALL ocultar o empty-state quando a coleção contiver um ou mais elementos.

#### Scenario: Lista com registros cadastrados
- **WHEN** a tela de listagem é carregada com uma lista que contém ao menos um registro
- **THEN** o sistema SHALL renderizar a tabela com os dados e SHALL NÃO renderizar o bloco de empty-state.

#### Scenario: Lista sem nenhum registro
- **WHEN** a tela de listagem é carregada com uma lista vazia
- **THEN** o sistema SHALL renderizar o bloco de empty-state e SHALL NÃO renderizar a tabela de dados.
