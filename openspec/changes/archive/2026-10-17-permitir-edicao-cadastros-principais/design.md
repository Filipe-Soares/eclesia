## Context

Ver `proposal.md` para motivação e entidades afetadas.

## Goals / Non-Goals

**Goals:**
- Adicionar endpoints `GET /.../{uuid}/editar` e `POST /.../{uuid}/editar` (ou `POST /.../{uuid}`) nos controladores web.
- Adicionar métodos `atualizar(UUID uuid, Request request)` nos serviços de aplicação (`PessoaApplicationService`, `OrganizacaoEclesialApplicationService`, `FuncaoEclesialApplicationService`, `VinculoPessoaOrganizacaoApplicationService`, `ProcessoMatrimonialApplicationService`, `ProjetoApplicationService`).
- Adequar os formulários HTML para exibir título contextual ("Nova" vs. "Editar") e ação do formulário dinâmico.
- Adicionar botões de edição nas tabelas de listagem e nas páginas de detalhes.

**Non-Goals:**
- Não alterar regras de chave primária imutável (UUID e ID numérico continuam inalterados).

## Decisions

1. **Decisão 1: Validação de CPF/CNPJ em Edição**
   - Na edição de Pessoa, se o CPF for mantido igual ao anterior da mesma pessoa, não disparar erro de unicidade (`existsByCpfAndUuidNot` ou checagem do UUID do proprietário).
2. **Decisão 2: Reutilização dos templates `form.html`**
   - Os templates existentes (`pessoa/form.html`, `organizacao/form.html`, etc.) receberão o objeto modelo preenchido com os dados existentes e uma variável `isEdicao` ou action contextual.

## Risks / Trade-offs

- [Risco] Conflito de CPF com outra pessoa ao alterar o campo.
- [Mitigação] Validar unicidade garantindo que o CPF informado pertença exclusivamente ao próprio registro ou esteja livre.
