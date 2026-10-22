## Why

A documentação de release do MVP está desalinhada com o código atual. `README_RELEASE_V16.10.7.md` e `CHECKLIST_RELEASE_V16.10.7.md` afirmam "99 testes" e que o código é idêntico à v16.10.6.1, mas a árvore de trabalho tem 136 testes (`@Test`, 34 classes) e mudanças pós-baseline (edição de cadastros e UI financeiro). O manual `Adm_Eclesial.pdf` também não referencia os fluxos de edição. Isso enfraquece a referência de homologação da release.

## What Changes

- Atualizar a contagem de testes de `99` para `136` em `README_RELEASE_V16.10.7.md` e `CHECKLIST_RELEASE_V16.10.7.md`.
- Ajustar a redação de baseline em `README_RELEASE_V16.10.7.md` ("mesmo código da v16.10.6.1", "não introduz novas funcionalidades") para refletir o estado pós-baseline.
- Revisar a afirmação "Sem alteração funcional na v16.10.7" em `CHECKLIST_RELEASE_V16.10.7.md` conforme a decisão de baseline.
- Atualizar o manual `Adm_Eclesial.pdf` a partir do `.docx` de origem fornecido pelo usuário, documentando os fluxos de edição de cadastros e corrigindo a contagem de testes.

Fora de escopo (não faz parte desta mudança):
- `pom.xml` e versionamento Maven (decisão: somente documentação).
- Código-fonte, migrations, regras de negócio e comportamento do sistema.

## Capabilities

### New Capabilities
<!-- Nenhuma: mudança exclusivamente documental, sem alteração de comportamento observável. -->

### Modified Capabilities
<!-- Nenhuma: nenhum requisito de especificação muda. -->

Esta mudança é docs-only. Como não altera comportamento de spec, o `.openspec.yaml` da mudança define `skip_specs: true`; nenhum delta de spec é criado.

## Impact

- Documentos de release: `README_RELEASE_V16.10.7.md`, `CHECKLIST_RELEASE_V16.10.7.md` e `Adm_Eclesial.pdf` (a partir do `.docx` de origem).
- Sem impacto em código, APIs, banco de dados, dependências ou comportamento do sistema.
- Dependência externa: o `.docx` de origem do manual precisa ser fornecido pelo usuário para a atualização do PDF.
