## 1. Ajustes nos Templates de Listagem Principais

- [x] 1.1 Atualizar `templates/pessoa/lista.html` e `templates/organizacao/lista.html` envolvendo a chamada de `empty-state` com `<th:block th:if="...">`
- [x] 1.2 Atualizar `templates/vinculo/lista.html`, `templates/usuario/lista.html`, `templates/funcao/lista.html` e `templates/funcao-vinculo/lista.html` com `<th:block th:if="...">`

## 2. Ajustes nos Templates Pastorais, Sacramentais e Financeiros

- [x] 2.1 Atualizar `templates/catequese/lista.html`, `templates/turma-catequese/lista.html`, `templates/etapa-catequese/lista.html`, `templates/inscricao-catequese/lista.html` e `templates/catequista-turma/lista.html`
- [x] 2.2 Atualizar `templates/sacramento/lista.html`, `templates/participante-sacramento/lista.html`, `templates/matrimonio/processo-lista.html` e `templates/projeto/lista.html`

## 3. Validação e Testes Automatizados

- [x] 3.1 Executar a suíte de testes completa (`mvn test`) garantindo sucesso na renderização de templates e testes existentes
