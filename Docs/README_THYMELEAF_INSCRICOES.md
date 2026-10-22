# Thymeleaf — Inscrições de Catequese

## Fluxo

```text
Catequese
  └── Etapa
       └── Turma
            └── Inscrições
                 ├── Lista
                 └── Nova inscrição
```

## Rotas

- `GET /catequeses/turmas/{turmaUuid}/inscricoes`
- `GET /catequeses/turmas/{turmaUuid}/inscricoes/nova`
- `POST /catequeses/turmas/{turmaUuid}/inscricoes`

## Regras

O cadastro utiliza o `InscricaoCatequeseApplicationService`, sem duplicar regra de negócio no controller.

- pessoa precisa existir;
- pessoa precisa estar ativa;
- turma precisa existir;
- turma deve estar `PLANEJADA` ou `ATIVA`;
- a pessoa não pode possuir inscrição `ATIVA` em outra turma da mesma etapa;
- a data da inscrição assume a data atual na abertura do formulário;
- o status inicial é `ATIVA`.

## Integração com PESSOA

A tela usa `PessoaApplicationService.listar()` para apresentar as pessoas ativas/cadastradas. A filtragem de atividade continua sendo responsabilidade do domínio/aplicação; a versão atual lista o cadastro completo para seleção e o serviço impede inscrição de pessoa inativa.

## Próximo refinamento

Quando o volume de pessoas crescer, substituir o `select` completo por busca paginada/autocomplete, sem alterar o contrato do Application Service.
