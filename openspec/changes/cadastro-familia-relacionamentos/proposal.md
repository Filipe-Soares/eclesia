## Why

O manual de regras e uso (`ADM_Eclesial.docx`, §5.2 e §5.3, §18.2) descreve o cadastro de família e relacionamentos entre pessoas, mas o sistema só os expõe em domínio, Application Services mínimos e REST parcial — **não há interface Web** (controllers, templates, menu) nem regras completas. Além disso, existem furos conhecidos: listagem de família sem recorte de organização, coerência recíproca de relacionamento não implementada, `data_inicio` de relacionamento inconsistente entre migration e entidade, e ausência total de testes nessa área.

## What Changes

- **UI de Família**: novo módulo `/familias` (lista, formulário, detalhe com membros) no estilo dos demais cadastros, com link no grupo "Cadastros" do menu e seção somente-leitura de família/relacionamentos na ficha da Pessoa.
- **Membros de família**: adicionar, listar por família e encerrar associação (com tipo e período), respeitando "família e pessoa ativas" e "sem associação ativa duplicada".
- **Relacionamentos entre pessoas**: criar, listar por pessoa, atualizar e remover, com criação automática do **par inverso** (inversão dependente de sexo da pessoa de destino).
- **Regras**: coerência recíproca de relacionamento na aplicação (§5.3); pré-checagem de membro ativo duplicado com mensagem amigável.
- **Autorização**: `Familia.listar` (Web e REST) passa a considerar apenas organizações autorizadas (via membros); família sem membros visível apenas a ADMINISTRADOR.
- **Consistência de dados**: alinhar `relacionamento_pessoa.data_inicio` para obrigatório (entidade + request), coerente com a migration existente.
- **API REST**: completar `GET /{uuid}` e `PUT` para famílias, membros e relacionamentos, no padrão dos demais módulos.
- **Testes**: cobrir criação/edição de família, membros, relacionamentos e a coerência recíproca.

Fora de escopo:
- Criar organização proprietária para família (contraria a limitação deliberada do §5.2).
- Alterar outros módulos ou regras de negócio fora de família/relacionamentos.

## Capabilities

### New Capabilities
- `familia-relacionamentos`: cadastro de família, membros de família e relacionamentos entre pessoas, com as regras dos §5.2/§5.3, autorização por organização e exposição em Web e REST.

### Modified Capabilities
<!-- Nenhuma: não há spec existente para família/relacionamentos a modificar. -->

## Impact

- Domínio/Infra: `Familia`, `MembroFamilia`, `RelacionamentoPessoa` e repositórios (ajustes pontuais).
- Application Services: `FamiliaApplicationService`, `MembroFamiliaApplicationService`, `RelacionamentoPessoaApplicationService` (ampliação).
- REST: `FamiliaRestController`, `MembroFamiliaRestController`, `RelacionamentoPessoaRestController`.
- Web: novos controllers `interfaces/web/familia`; templates `templates/familia/**`; `templates/fragments/layout.html`; `templates/pessoa/detalhe.html`.
- Segurança: `SecurityConfig` (rotas `/familias/**` já reservadas — confirmar/ajustar).
- Sem novas migrations (tabelas já existem); possível ajuste de constraint já prevista.
