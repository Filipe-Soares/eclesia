# ADM Eclesial v16.10.2 — Formulários, mensagens e validação UX

## Escopo
- Padronização inicial dos formulários de cadastro prioritários.
- Erros de Bean Validation exibidos junto aos campos.
- Erros globais/regras de negócio preservados e destacados.
- Valores submetidos continuam preservados pelo binding Thymeleaf/Spring MVC.
- Ações Salvar/Cancelar padronizadas.
- Feedback global reutilizável para sucesso/erro via fragment Thymeleaf.
- Flash messages adicionadas aos fluxos principais de Pessoa, Organização e Catequese.

## Formulários priorizados
Pessoa, Organização Eclesial, Função Eclesial, Vínculo Pessoa-Organização, Vínculo de Função, Catequese, Etapa, Turma e Inscrição.

## Fora do escopo
Nenhuma migration, nova tabela ou alteração de regra de negócio. A padronização dos demais formulários continua incrementalmente nas próximas entregas UX.
