# ADM Eclesial

Projeto inicial do sistema de gestão eclesial, com **Java 21 + Spring Boot 3 + PostgreSQL**, preparado para uso com Thymeleaf e futura substituição da camada web por React/Next.js.

## Arquitetura

```text
interfaces/rest       -> API REST /api/v1
interfaces/web        -> Thymeleaf
          \           /
           Application Services
                  |
                Domain
                  |
            Persistence JPA
                  |
             PostgreSQL
```

REST e Thymeleaf utilizam os mesmos serviços de aplicação. A regra de negócio não depende de HTTP nem de Thymeleaf.

## Banco

- schema PostgreSQL: `adm_eclesial`
- PK interna: `BIGINT GENERATED ALWAYS AS IDENTITY`
- UUID externo para APIs
- Flyway para migrations
- `spring.jpa.hibernate.ddl-auto=validate`
- enums do domínio como `VARCHAR + CHECK` no banco e `enum` em Java
- nenhum trigger nesta versão
- migration inicial: `V1__estrutura_inicial.sql`

## Fatias implementadas

### Pessoa

REST:

- `POST /api/v1/pessoas`
- `GET /api/v1/pessoas`
- `GET /api/v1/pessoas/{uuid}`

Web:

- `/pessoas`
- `/pessoas/nova`
- `/pessoas/{uuid}`

### Organização eclesial

REST:

- `POST /api/v1/organizacoes-eclesiais`
- `GET /api/v1/organizacoes-eclesiais`
- `GET /api/v1/organizacoes-eclesiais/{uuid}`
- `GET /api/v1/organizacoes-eclesiais/tipos`

Web:

- `/organizacoes-eclesiais`
- `/organizacoes-eclesiais/nova`
- `/organizacoes-eclesiais/{uuid}`

### Função eclesial

REST:

- `POST /api/v1/funcoes-eclesiais`
- `GET /api/v1/funcoes-eclesiais`
- `GET /api/v1/funcoes-eclesiais/{uuid}`

Web:

- `/funcoes-eclesiais`
- `/funcoes-eclesiais/nova`
- `/funcoes-eclesiais/{uuid}`

### Vínculo pessoa × organização

REST:

- `POST /api/v1/vinculos-pessoa-organizacao`
- `GET /api/v1/vinculos-pessoa-organizacao`
- `GET /api/v1/vinculos-pessoa-organizacao/{uuid}`

Web:

- `/vinculos-pessoa-organizacao`
- `/vinculos-pessoa-organizacao/novo`
- `/vinculos-pessoa-organizacao/{uuid}`

### Vínculo de função eclesial

REST:

- `POST /api/v1/vinculos-funcoes-eclesiais`
- `GET /api/v1/vinculos-funcoes-eclesiais`
- `GET /api/v1/vinculos-funcoes-eclesiais/{uuid}`

Web:

- `/vinculos-funcoes-eclesiais`
- `/vinculos-funcoes-eclesiais/novo`
- `/vinculos-funcoes-eclesiais/{uuid}`

## Regras de negócio implementadas

1. Pessoa e organização precisam existir antes do vínculo.
2. Pessoa inativa não pode receber novo vínculo.
3. Organização eclesial inativa não pode receber novo vínculo.
4. O período deve ser válido: `data_fim >= data_inicio`.
5. Uma pessoa pode possuir vários vínculos ao longo do tempo.
6. Uma pessoa pode possuir várias funções simultaneamente na mesma `ORGANIZACAO_ECLESIAL`.
7. A função atribuída precisa estar ativa.
8. O vínculo pessoa-organização precisa estar ativo para receber função.
9. O período da função deve estar contido no período do vínculo pessoa-organização.
10. A mesma função não pode ser atribuída duas vezes com períodos sobrepostos no mesmo vínculo.
11. Uma função marcada como `exclusivo_na_organizacao` não pode estar atribuída simultaneamente na mesma organização a outra atribuição.
12. Um vínculo pessoa-organização pode ter somente uma função principal em períodos sobrepostos.
13. Um vínculo pessoa-organização pode ser principal somente quando não houver outro vínculo principal conflitante para a mesma pessoa e organização.
14. Códigos de `FUNCAO_ECLESIAL` são únicos e normalizados para maiúsculas.
15. UUID é utilizado nas APIs; IDs internos continuam sendo `BIGINT`.

## Testes automatizados

A camada de aplicação possui testes unitários para as regras dos vínculos:

- criação de vínculo principal;
- períodos inválidos;
- conflito de vínculo principal;
- pessoa inativa;
- organização inativa;
- múltiplas funções simultâneas na mesma organização;
- função inativa;
- vínculo suspenso/inativo;
- função fora do período do vínculo;
- mesma função com período sobreposto;
- função exclusiva já atribuída;
- conflito de função principal;
- normalização e duplicidade do código da função.

Arquivos principais:

- `VinculoPessoaOrganizacaoApplicationServiceTest`
- `VinculoFuncaoApplicationServiceTest`
- `FuncaoEclesialApplicationServiceTest`
- `AdmEclesialApplicationTests`

## Revisão técnica realizada

Foram conferidos estaticamente:

- `pom.xml` como XML válido;
- Java 21;
- mapeamentos JPA das entidades implementadas contra a migration V1;
- nomes e tamanhos de colunas relevantes;
- `TipoVinculoOrganizacao` alinhado a `VARCHAR(20)` do PostgreSQL;
- zero triggers na migration;
- ausência de referências a `PAPEL` no código;
- rotas Thymeleaf com seus templates correspondentes;
- página de detalhe de vínculo de função criada;
- consultas JPQL de conflito de períodos;
- validação básica de estrutura dos arquivos Java.

### Limitação do ambiente de revisão

O ambiente utilizado para gerar/revisar este pacote possui Java 21, mas **não possui Maven, Maven Wrapper ou uma distribuição local das dependências Maven**. Além disso, não há acesso de rede para baixar Maven/dependências. Portanto, `mvn clean test` **não pôde ser executado aqui**.

Isso significa que a revisão confirma a consistência estrutural e lógica do código, mas não substitui a execução real do build em uma máquina com Maven e PostgreSQL.

## Como validar localmente

Pré-requisitos:

- JDK 21
- Maven 3.9+
- PostgreSQL compatível

Banco padrão configurado no projeto:

```text
URL: jdbc:postgresql://localhost:5432/adm_eclesial
Usuário: postgres
Senha: postgres
```

Ou configure:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

Depois:

```bash
mvn clean test
```

Para iniciar:

```bash
mvn spring-boot:run
```

A aplicação utiliza Flyway para criar/evoluir o schema e Hibernate em modo `validate` para verificar os mapeamentos JPA.

## Próxima etapa

Com esta fatia validada, o próximo passo de domínio é avançar para **Catequese**, mantendo REST + Thymeleaf e os mesmos padrões de testes. Antes disso, no primeiro ambiente com PostgreSQL disponível, deve ser executado o `mvn clean test` para validar efetivamente o contexto Spring, Flyway e `ddl-auto=validate`.

## Testes de integração

O teste `AdmEclesialApplicationTests` usa Testcontainers com PostgreSQL 16 para validar o contexto completo, incluindo Flyway e `hibernate.ddl-auto=validate`, sem depender das credenciais do PostgreSQL local.

É necessário ter Docker em execução para executar a suíte completa:

```bash
mvn clean test
```

Os testes unitários dos serviços não dependem de PostgreSQL.

## Configuração do PostgreSQL da aplicação

Para execução normal, as credenciais podem ser informadas por variáveis de ambiente:

```text
DB_URL=jdbc:postgresql://localhost:5432/adm_eclesial
DB_USERNAME=postgres
DB_PASSWORD=sua_senha
SERVER_PORT=8080
```

## Thymeleaf — Turmas da Catequese

A v12 adiciona o fluxo Web de Turmas abaixo de uma Etapa:

`/catequeses/etapas/{etapaUuid}/turmas`

Inclui listagem, criação e edição, reutilizando `TurmaCatequeseApplicationService`.

## v16.4 — fechamento Matrimonio/Sacramentos + Financeiro MVP

### Matrimonio / Sacramento
- `MATRIMONIO.sacramento_id` integra o casamento ao sacramento correspondente.
- O fluxo matrimonial cria o `SACRAMENTO` de tipo `MATRIMONIO` e os dois nubentes como `CONJUGE`.
- A API genérica de sacramentos não cria/altera `MATRIMONIO`.
- Celebração e processo devem pertencer à mesma `ORGANIZACAO_ECLESIAL`.
- Transições básicas de status do processo matrimonial são validadas.

### Financeiro
- `CONTRIBUICAO` exige `CONTA_FINANCEIRA` e gera `MOVIMENTACAO_FINANCEIRA` + `CONTRIBUICAO_MOVIMENTACAO`.
- `DIZIMISTA` possui CRUD REST básico e valida vigência/organização/pessoa.
- `CONTA_A_PAGAR` cria a primeira `PARCELA_A_PAGAR` automaticamente.
- `OBRIGACAO_REPASSE` cria `CONTA_A_PAGAR` + parcela automaticamente.
- `PAGAMENTO` exige alocações completas e gera saída financeira + `PAGAMENTO_MOVIMENTACAO`.
- Status de parcela e conta são atualizados a partir das alocações.
- `TRANSFERENCIA_FINANCEIRA` gera as movimentações de saída e entrada vinculadas.
- Centro de custo é validado contra a organização da conta financeira.
- Percentual de regra de repasse limitado a 0–100.

> Esta versão foi preparada para validação pelo Maven/Docker do ambiente do projeto. O ambiente de geração não possui Maven nem Docker disponíveis, portanto o `BUILD SUCCESS` da v16.4 ainda não foi afirmado aqui.

## v16.5 — Revisão financeira e Orçamento

A v16.5 revisa regras críticas do financeiro e adiciona o módulo inicial de Orçamento (`ORCAMENTO` + `ITEM_ORCAMENTO`).


## v16.6 — Projeto/Obra + Recursos Vinculados

Implementados `PROJETO`, `RECURSO_VINCULADO`, `FONTE_RECURSO_PROJETO` e `UTILIZACAO_RECURSO`, além do fluxo de `DESTINACAO_CONTRIBUICAO` com finalidade específica.

Detalhes: `README_PROJETO_RECURSO_V16.6.md`.
