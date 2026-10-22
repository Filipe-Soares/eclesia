# Thymeleaf — Etapas da Catequese

Esta etapa acrescenta a interface Web de `ETAPA_CATEQUESE` sobre a implementação de Catequese já existente.

## Rotas

- `GET /catequeses/{catequeseUuid}/etapas` — lista as etapas da Catequese.
- `GET /catequeses/{catequeseUuid}/etapas/nova` — formulário de inclusão.
- `POST /catequeses/{catequeseUuid}/etapas` — cria uma etapa.
- `GET /catequeses/{catequeseUuid}/etapas/{uuid}/editar` — formulário de edição.
- `POST /catequeses/{catequeseUuid}/etapas/{uuid}` — atualiza uma etapa.

## Regras mantidas

- Nome da etapa é único dentro da Catequese, ignorando maiúsculas/minúsculas.
- Ordem da etapa é única dentro da Catequese.
- Na edição, a própria etapa é excluída da verificação de duplicidade.
- A Catequese precisa existir.
- `ativo` assume `true` quando não informado na criação.

## Fluxo visual

```text
Catequese
   |
   +-- Gerenciar etapas
          |
          +-- Lista de etapas
          |      |
          |      +-- Nova etapa
          |      +-- Editar
          |
          +-- Voltar para a Catequese
```

## Arquivos principais

- `interfaces/web/catequese/EtapaCatequeseWebController.java`
- `templates/etapa-catequese/lista.html`
- `templates/etapa-catequese/form.html`
- `application/catequese/EtapaCatequeseApplicationService.java`

A camada Web continua chamando diretamente o Application Service. Não há chamada HTTP interna da tela Thymeleaf para a API REST.
