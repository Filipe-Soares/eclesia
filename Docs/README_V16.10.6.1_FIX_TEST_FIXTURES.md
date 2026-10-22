# ADM Eclesial v16.10.6.1 — Fix de fixtures de testes

Patch sobre a v16.10.6-correcao-isolamento-release.

## Escopo

Somente fixtures de testes foram ajustados para refletir a invariável organizacional introduzida pela revisão de isolamento.

Arquivos ajustados:
- EtapaCatequeseApplicationServiceTest
- InscricaoCatequeseApplicationServiceTest
- ParticipanteSacramentoApplicationServiceTest
- RegistroSacramentalApplicationServiceTest
- ProcessoMatrimonialApplicationServiceTest

## Garantias

- Nenhuma alteração em src/main.
- Nenhuma migration Flyway.
- Nenhum teste removido ou desabilitado.
- Regras de negócio e isolamento A→B permanecem inalterados.
- Fixtures de Catequese/Turma/Sacramento/Processo Matrimonial agora possuem organização coerente com os casos de uso protegidos.

## Checkpoint

Executar:

```bash
mvn clean test
```

A versão só deve ser considerada estável após BUILD SUCCESS no ambiente Maven do projeto.
