## Context

No Thymeleaf 3, invocações de fragmentos que especificam parâmetros posicionais (`th:replace="~{fragments/layout :: empty-state('Texto')}"`) exigem correspondência exata na assinatura do fragmento (`th:fragment="empty-state(title, message)"`). Caso o chamador passe 1 argumento e a assinatura declare 2, uma `TemplateProcessingException` é lançada durante o parsing do template.

## Goals / Non-Goals

**Goals:**
- Ajustar `fragments/layout.html` para que o fragmento `empty-state(title, message)` ofereça fallbacks adequados.
- Padronizar todas as chamadas em templates de listagem (`vinculo`, `sacramento`, `projeto`, `matrimonio`, `participante-sacramento`, `inscricao-catequese`, `etapa-catequese`, `catequista-turma`) para passar sempre `(title, message)` de forma consistente e expressiva.
- Adicionar testes automatizados de renderização de páginas Web / Controller para capturar erros de parsing do Thymeleaf durante o `mvn test`.

**Non-Goals:**
- Não alterar lógica de negócio ou estrutura dos DTOs.

## Decisions

1. **Decisão 1: Manter a assinatura canônica `empty-state(title, message)` e padronizar as chamadas**
   - *Rationale*: A assinatura de 2 parâmetros é semanticamente mais rica, pois permite um título claro (ex: "Nenhum processo matrimonial cadastrado.") e um subtítulo/mensagem de instrução para o usuário (ex: "Cadastre um processo matrimonial para iniciar o acompanhamento.").
   - *Alternativas*: Criar fragmentos com nomes diferentes como `empty-state-simple(message)` (rejeitado por poluir o arquivo de layout e criar duplicações desnecessárias).

2. **Decisão 2: Adicionar testes de integração WebMvcTest para os controladores web**
   - *Rationale*: Testar a renderização dos templates de lista (`matrimonio/processo-lista`, `sacramento/lista`, `projeto/lista`, etc.) garante que erros de parsing do Thymeleaf sejam identificados imediatamente nos testes do build.

## Risks / Trade-offs

- [Risco] Existir alguma outra página com chamada de 1 parâmetro.
- [Mitigação] Varredura automatizada com `grep` em todo o diretório `templates/` para garantir conformidade de 100% das chamadas.
