## Why

Ao salvar uma nova pessoa ou organização eclesial, o sistema redireciona para a página de detalhes correspondente (`/pessoas/{uuid}` ou `/organizacoes-eclesiais/{uuid}`). O serviço de segurança dispara uma exceção `AccessDeniedException` (Acesso Negado) porque:
1. Novas pessoas ainda não possuem registros na tabela `vinculo_pessoa_organizacao_eclesial`, tornando-as invisíveis aos filtros de organização.
2. Novas organizações ainda não constam na tabela `usuario_organizacao` do usuário logado, mesmo quando este é o Administrador do sistema (`ROLE_ADMINISTRADOR`).

É necessário garantir que administradores possuam acesso institucional pleno a todas as organizações e que pessoas recém-criadas sem vínculo sejam acessíveis por perfis autorizados (Administrador e Secretaria).

## What Changes

- Permitir que o perfil `ROLE_ADMINISTRADOR` tenha acesso a todas as organizações ativas no `SegurancaOrganizacaoService`.
- Permitir que pessoas sem vínculo eclesiástico possam ser visualizadas e gerenciadas por usuários com permissão (`ADMINISTRADOR` e `SECRETARIA`) em `PessoaApplicationService`.
- Ajustar consultas e métodos de validação no `PessoaRepository` e `SegurancaOrganizacaoService`.

## Capabilities

### New Capabilities
- `seguranca-acesso`: Gestão de autorizações e visibilidade institucional para perfis administrativos e cadastros sem vínculo prévio.

## Impact

- `src/main/java/br/com/admeclesial/application/usuario/SegurancaOrganizacaoService.java`
- `src/main/java/br/com/admeclesial/application/pessoa/PessoaApplicationService.java`
- `src/main/java/br/com/admeclesial/infrastructure/persistence/pessoa/PessoaRepository.java`
- Testes automatizados unitários e de integração
