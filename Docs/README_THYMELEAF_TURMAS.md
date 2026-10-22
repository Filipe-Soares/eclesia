# Thymeleaf — Turmas da Catequese

## Fluxo

```text
Catequese
  └── Etapas
       └── Turmas
```

## Rotas

- `GET /catequeses/etapas/{etapaUuid}/turmas`
- `GET /catequeses/etapas/{etapaUuid}/turmas/nova`
- `POST /catequeses/etapas/{etapaUuid}/turmas`
- `GET /catequeses/etapas/{etapaUuid}/turmas/{uuid}/editar`
- `POST /catequeses/etapas/{etapaUuid}/turmas/{uuid}`

## Campos

Nome, ano, turno, período, capacidade, local, status e observação.

A organização eclesial não é digitada na tela: ela deve ser a mesma organização da Catequese à qual a Etapa pertence. O serviço de aplicação continua responsável por validar essa compatibilidade.

## Regras preservadas

- etapa deve existir e estar ativa;
- organização deve existir e estar ativa;
- turma deve pertencer à mesma organização da Catequese;
- período deve ser válido;
- capacidade, quando informada, deve ser positiva;
- status padrão é `PLANEJADA`.
