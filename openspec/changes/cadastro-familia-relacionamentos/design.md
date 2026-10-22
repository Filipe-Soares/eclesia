## Context

Família e relacionamentos já existem em domínio (`Familia`, `MembroFamilia`, `RelacionamentoPessoa`), repositórios e tabelas (V1), mas a camada Web não existe e as regras estão incompletas. Ver `proposal.md` — Why para a motivação. Restrições relevantes do projeto: `SecurityConfig` já reserva `/familias/**` (leitura para os 5 perfis; escrita para ADMINISTRADOR e SECRETARIA); autorização por organização é feita via `SegurancaOrganizacaoService`; o padrão de módulo é `@Controller` + templates Thymeleaf + `@RestController` sobre os mesmos Application Services.

## Goals / Non-Goals

**Goals:**
- Fechar §5.2 e §5.3 do manual de ponta a ponta: UI + regras + API + testes.
- Corrigir os furos 3.1 (recorte de organização), 3.2 (coerência recíproca), 3.3 (`data_inicio`), 3.4 (duplicidade amigável).

**Non-Goals:**
- Criar organização proprietária para família (limitação deliberada do §5.2).
- Alterar modelos/regras fora de família e relacionamentos.
- Criar novas tabelas ou migrations (estrutura já existe).

## Decisions

### D1 — `relacionamento_pessoa.data_inicio` obrigatório
A entidade passa a declarar `nullable=false` e o `RelacionamentoPessoaRequest` marca `@NotNull` em `dataInicio`, alinhando app e banco (migration já é `DATE NOT NULL`).
- Alternativa descartada: relaxar a coluna no banco. Implicaria nova migration e um relacionamento sem data de início, incoerente com §5.3 ("Datas devem formar período válido").

### D2 — Autorização de família derivada dos membros
A visibilidade de uma família deriva das organizações das pessoas associadas como membros (mesma lógica já usada por `MembroFamilia`/`Relacionamento`). Família sem membros fica visível apenas a ADMINISTRADOR.
- Alternativa descartada: adicionar `organizacao_eclesial_id` à família — contraria o §5.2 ("família vazia não possui organização proprietária").
- Implementação: um método de consulta que retorna os ids de família com membros cujas pessoas são visíveis nas organizações autorizadas; ADMINISTRADOR recebe todas. Aplicado em `FamiliaApplicationService` (usado por Web e REST).

### D3 — Coerência recíproca com criação automática do inverso (sexo-aware)
Ao criar um relacionamento, o serviço cria o inverso, usando `Pessoa.sexo` (`TipoSexo`) quando o tipo inverso depende de sexo. Ao remover, remove o par inverso. Um par já existente não é duplicado.

```
TIPO(origem->destino)   INVERSO(destino->origem)
PAI                     FILHO
MAE                     FILHO
FILHO                   PAI/MAE   (sexo do destino)
FILHA                   PAI/MAE   (sexo do destino)
CONJUGE                 CONJUGE   (simetrico)
IRMAO                   IRMAO/IRMA (sexo do destino)
IRMA                    IRMAO/IRMA (sexo do destino)
AVO                     NETO/NETA (sexo do destino)
AVA                     NETO/NETA (sexo do destino)
NETO                    AVO/AVA   (sexo do destino)
NETA                    AVO/AVA   (sexo do destino)
OUTRO                   OUTRO
```
- Alternativa descartada: apenas validar consistência sem gravar o inverso (menos útil na UI e deixa a reciprocidade a cargo do usuário).
- Sexo `NAO_INFORMADO` no destino: usar o tipo neutro do par (FILHO, IRMAO, NETO) e registrar a decisão.

### D4 — Forma da UI
Módulo dedicado `/familias` (lista, formulário, detalhe com membros) sob "Cadastros", mais uma seção de leitura de família/relacionamentos na ficha da Pessoa, com atalhos para adicionar membro/relacionamento.
- Alternativa descartada: apenas seção na Pessoa (não cobre famílias vazias nem a gestão de membros de forma autônoma).

### D5 — Ampliação de serviços e paridade REST
Completar `FamiliaApplicationService` (buscar, atualizar, inativar, listarPorFamilia de membros), `MembroFamiliaApplicationService` (listarPorFamilia, encerrar, buscar) e `RelacionamentoPessoaApplicationService` (buscar, atualizar, remover, porPessoa). REST ganha `GET /{uuid}` e `PUT` conforme o padrão de Pessoa/Organização, reutilizando os mesmos serviços.

### D6 — Duplicidade de membro com mensagem amigável
Pré-checar no serviço a existência de associação ativa (mesma família/pessoa, `data_fim` nula) antes do `save`, retornando erro de negócio em vez de `DataIntegrityViolation`. O índice parcial único permanece como salvaguarda.

## Risks / Trade-offs

- [Filtragem em memória pode ficar cara com volume alto] → seguir o padrão atual (findAll + filtro) nesta mudança; otimizar com `@Query` fica para depois, se necessário.
- [Inversão sexo-aware com `NAO_INFORMADO`] → definir tipo neutro e documentar; evitar bloquear o cadastro.
- [Duplicidade de par inverso] → verificar existência antes de criar; cobrir com teste.
- [Sem migration] → mantém o banco estável; a única correção de schema é de mapeamento/validação (D1), sem DDL.

## Migration Plan

- Nenhuma migration nova. Deploy é apenas código.
- Rollback: reverter os arquivos; o banco permanece compatível (estrutura inalterada).

## Open Questions

Nenhuma pendente que altere specs, abordagem ou tarefas.
