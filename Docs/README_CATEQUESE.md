# Módulo de Catequese — MVP

O módulo foi integrado ao starter v7 mantendo a arquitetura existente.

## Tabelas

- `catequese`
- `etapa_catequese`
- `turma_catequese`
- `inscricao_catequese`
- `catequista_turma`

## Fluxo

```text
ORGANIZACAO_ECLESIAL
        |
        v
    CATEQUESE
        |
        v
ETAPA_CATEQUESE
        |
        v
TURMA_CATEQUESE
      /       \
     v         v
INSCRICAO   CATEQUISTA
CATEQUESE     TURMA
```

## Endpoints REST

- `POST /api/v1/catequeses`
- `GET /api/v1/catequeses`
- `GET /api/v1/catequeses/{uuid}`
- `POST /api/v1/etapas-catequese`
- `GET /api/v1/etapas-catequese?catequeseUuid={uuid}`
- `GET /api/v1/etapas-catequese/{uuid}`
- `POST /api/v1/turmas-catequese`
- `GET /api/v1/turmas-catequese`
- `GET /api/v1/turmas-catequese/{uuid}`
- `GET /api/v1/turmas-catequese/por-etapa?etapaUuid={uuid}`
- `POST /api/v1/inscricoes-catequese`
- `GET /api/v1/inscricoes-catequese/{uuid}`
- `GET /api/v1/inscricoes-catequese/por-turma?turmaUuid={uuid}`
- `POST /api/v1/catequistas-turma`
- `GET /api/v1/catequistas-turma/por-turma?turmaUuid={uuid}`

## Regras principais

- A turma deve pertencer à mesma organização da catequese.
- Não é permitido inscrever pessoa inativa.
- Não é permitido inscrever em turma encerrada ou cancelada.
- A pessoa não pode possuir inscrição ativa em outra turma da mesma etapa.
- Catequista precisa possuir a função eclesial `CATEQUISTA` ativa na organização da turma.
- Uma turma pode possuir vários catequistas.
- Existe no máximo um catequista principal ativo por turma/período.
- Família não participa da inscrição no MVP.

## Fora do MVP

- `ENCONTRO_CATEQUESE`
- `FREQUENCIA_CATEQUESE`
- avaliações
- conteúdo pedagógico
- certificados

## Migration

A `V1__estrutura_inicial.sql` do starter v7 já criava uma primeira versão das tabelas de Catequese. Por isso a evolução correta é a `V2__ajustes_catequese.sql`, que ajusta o modelo sem recriar as tabelas.

## Testes de negócio

Foram adicionados 23 testes unitários dos Application Services de Catequese:

- `CatequeseApplicationServiceTest`: organização ativa, período válido e status padrão.
- `EtapaCatequeseApplicationServiceTest`: nome e ordem únicos dentro da catequese.
- `TurmaCatequeseApplicationServiceTest`: organização/etapa ativas, compatibilidade organizacional e período.
- `InscricaoCatequeseApplicationServiceTest`: pessoa ativa, turma elegível, duplicidade por etapa e status padrão.
- `CatequistaTurmaApplicationServiceTest`: função CATEQUISTA, pessoa ativa, catequista principal e múltiplos catequistas.

Esses testes são unitários e usam Mockito; não dependem de PostgreSQL ou Docker. O teste de contexto existente continua responsável por validar Flyway + PostgreSQL + Hibernate.
