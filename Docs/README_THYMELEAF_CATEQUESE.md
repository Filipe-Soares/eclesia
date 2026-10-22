# Thymeleaf — Catequese

Esta etapa adiciona a primeira camada Web da Catequese, sem alterar a API REST existente.

## Rotas

- `GET /catequeses` — listagem
- `GET /catequeses/nova` — formulário de cadastro
- `POST /catequeses` — criação
- `GET /catequeses/{uuid}` — detalhe
- `GET /catequeses/{uuid}/editar` — formulário de edição
- `POST /catequeses/{uuid}` — atualização

## Arquitetura

```text
Thymeleaf
   |
   v
CatequeseWebController
   |
   v
CatequeseApplicationService
   |
   +--> CatequeseRepository
   +--> OrganizacaoEclesialRepository
```

O Thymeleaf **não chama a API REST da própria aplicação**. A camada Web e a API REST utilizam o mesmo Application Service.

## Templates

```text
src/main/resources/templates/catequese/
├── lista.html
├── form.html
└── detalhe.html
```

## Próxima etapa

Depois da validação desta tela, a navegação de Catequese será ampliada gradualmente para:

```text
Catequese
   └── Etapas
         └── Turmas
               ├── Inscrições
               └── Catequistas
```
