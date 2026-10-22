## Context

O isolamento multitenancy do sistema é verificado por `SegurancaOrganizacaoService` e por consultas com `@Query` em `PessoaRepository`. Atualmente:
1. `SegurancaOrganizacaoService.podeAcessar` consulta apenas `UsuarioOrganizacaoRepository`. Usuários com perfil `ADMINISTRADOR` não tinham bypass ou lista completa de organizações ativas.
2. `PessoaRepository.isVisivel` e `PessoaApplicationService.buscar` exigem que a pessoa pertença ao conjunto de organizações autorizadas. Pessoas recém-criadas que não têm vínculo na tabela `vinculo_pessoa_organizacao_eclesial` falham nessa verificação.

## Goals / Non-Goals

**Goals:**
- Conceder a usuários `ROLE_ADMINISTRADOR` acesso a todas as organizações ativas no `SegurancaOrganizacaoService`.
- Permitir que perfis autorizados de cadastro (`ADMINISTRADOR` e `SECRETARIA`) acessem pessoas sem vínculo (pessoas recém-criadas ou cadastradas antes da vinculação).
- Atualizar a suíte de testes unitários de segurança e serviços.

**Non-Goals:**
- Não remover o isolamento multitenancy para outros perfis setoriais (ex: financeiro ou catequista restrito à sua respectiva paróquia/comunidade).

## Decisions

1. **Decisão 1: Checagem de perfil no `SegurancaOrganizacaoService`**
   - No `usuarioAtual()`, verificar se possui perfil `ADMINISTRADOR` (ou via `Authentication.getAuthorities()`). Se for `ADMINISTRADOR`, `podeAcessar(uuid)` retorna `true` para qualquer organização ativa, e `organizacoesAutorizadas()` retorna os UUIDs de todas as organizações ativas.

2. **Decisão 2: Consulta e validação de pessoas sem vínculo**
   - No `PessoaApplicationService.buscar(uuid)`: se o usuário logado for `ADMINISTRADOR` ou se a pessoa não tiver nenhum vínculo (ou se tiver vínculo nas organizações autorizadas), permitir a visualização.
   - No `PessoaRepository.isVisivel`, verificar se a pessoa não possui nenhum vínculo cadastrado OU possui vínculo em uma das organizações autorizadas.

## Risks / Trade-offs

- [Risco] Permitir que operadores de outras organizações vejam pessoas com vínculos restritos de paróquias terceiras.
- [Mitigação] A regra de flexibilização aplica-se apenas quando a pessoa NÃO possui nenhum vínculo registrado ou quando o usuário é Administrador global.
