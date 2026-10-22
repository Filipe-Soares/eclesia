## 1. Reconciliação dos documentos de release

- [x] 1.1 Trocar a contagem de testes de `99` para `136` em `README_RELEASE_V16.10.7.md` (linha 9) e `CHECKLIST_RELEASE_V16.10.7.md` (linha 11); verificar buscando `99 testes` nesses arquivos e confirmando que não há mais ocorrências
- [x] 1.2 Ajustar a redação de baseline em `README_RELEASE_V16.10.7.md` (linhas 6 e 14) para refletir o estado pós-baseline (edição de cadastros e UI financeiro), removendo as afirmações "mesmo código da v16.10.6.1" e "não introduz novas funcionalidades"; verificar relendo o arquivo final
- [x] 1.3 Revisar a afirmação "Sem alteração funcional na v16.10.7" em `CHECKLIST_RELEASE_V16.10.7.md` (linha 5) conforme a decisão de baseline; verificar relendo o arquivo final

## 2. Atualização do manual (PDF)

- [x] 2.1 Obter o `.docx` de origem do manual — `ADM_Eclesial.docx` disponível na raiz do projeto (verificado: conteúdo bate com `Adm_Eclesial.pdf`)
- [x] 2.2 Em `ADM_Eclesial.docx`, corrigir a contagem de testes de 99 para 136 (linha "Base técnica: v16.10.6.1 • 99 testes • BUILD SUCCESS") e revisar a afirmação de baseline "A release v16.10.7 não altera o código validado na v16.10.6.1"; documentar os fluxos de edição de cadastros (pessoa, organização, função, vínculo, projeto e processo matrimonial); verificar revisando o texto editado
- [x] 2.3 Regerar `Adm_Eclesial.pdf` a partir do `ADM_Eclesial.docx` atualizado com LibreOffice headless (`soffice --headless --convert-to pdf ADM_Eclesial.docx`) e substituir o arquivo; verificar abrindo o PDF gerado e conferindo a seção de edição e a contagem de testes

## 3. Validação final

- [x] 3.1 Executar `openspec validate --change reconciliar-documentacao-release-v16-10-7` e confirmar que a mudança valida com `skip_specs` documentado
- [x] 3.2 Revisar os três documentos e confirmar a ausência de referências residuais a "99 testes" e de afirmações de baseline desatualizadas
