# ADM Eclesial — Relatório Técnico v16.10.6

**Baseline auditada:** v16.10.5.3 — BUILD SUCCESS informado pelo usuário  
**Objetivo:** revisão de segurança/isolamento A→B seguida de arquitetura e dependências, sem ampliação do MVP.

## 1. Resumo executivo

A baseline compila e possui um núcleo correto de autorização organizacional (`SegurancaOrganizacaoService`), com vínculo explícito `USUARIO_ORGANIZACAO` e sem privilégio global implícito para `ADMINISTRADOR`. Relatórios e dashboards relevantes chamam `validarAcesso` antes das consultas.

Entretanto, a auditoria encontrou **bloqueantes de isolamento organizacional** em fluxos operacionais. A proteção do `SecurityFilterChain` controla perfil/rota, mas vários controllers REST e alguns fluxos web chamam Application Services que não validam a organização do recurso. Há também listagens globais (`listar()`/`findAll()`) em módulos organizacionais. Portanto, um usuário com perfil funcional válido para a organização A pode potencialmente consultar ou alterar recursos de B se conhecer UUIDs ou alcançar listagens globais.

**Conclusão de release:** v16.10.5.3 continua sendo a baseline estável de compilação, mas **não deve ser promovida para v16.10.7 enquanto os achados BLOQUEANTES abaixo não forem corrigidos e cobertos por testes A→B fim a fim**.

## 2. Escala de risco

- 🔴 **BLOQUEANTE** — pode violar isolamento, segurança, integridade ou operação essencial; obrigatório antes do release.
- 🟠 **IMPORTANTE** — dívida técnica/configuração relevante; corrigir quando seguro ou documentar explicitamente.
- 🟢 **PÓS-MVP** — melhoria válida sem necessidade para estabilidade do MVP.
- ⚪ **SEM ACHADO** — revisado e adequado ao MVP.

## 3. Revisão de segurança

### 3.1 Autenticação, sessão e CSRF — ⚪ SEM ACHADO

`SecurityConfig` usa Spring Security com login por sessão, BCrypt, CSRF mantido pelo comportamento padrão e logout configurado. Não foi encontrada desativação global de CSRF. O endpoint público de actuator está limitado a `/actuator/health` na regra de autorização.

### 3.2 Matriz de perfis no SecurityFilterChain — ⚪/🟠

As rotas reais principais estão mapeadas para os perfis definidos (`ADMINISTRADOR`, `SECRETARIA`, `CATEQUESE`, `FINANCEIRO`, `CONSULTA`). A correção anterior de nomes reais como `/organizacoes-eclesiais/**` está presente.

**Limitação importante:** autorização por perfil não equivale a autorização por organização. O filtro permite chegar ao controller quando o perfil é válido; a organização precisa ser validada separadamente.

### 3.3 SegurancaOrganizacaoService — ⚪ SEM ACHADO

O serviço:
- resolve o usuário autenticado pelo login;
- exige usuário ativo;
- consulta `USUARIO_ORGANIZACAO`;
- não concede acesso global ao ADMINISTRADOR;
- retorna somente UUIDs explicitamente vinculados;
- lança `AccessDeniedException` quando o vínculo não existe.

Os testes existentes cobrem A permitido e B negado diretamente no serviço.

### 3.4 Relatórios e dashboards — ⚪ SEM ACHADO relevante

Os Application Services de relatórios auditados e `DashboardApplicationService` chamam `seguranca.validarAcesso(organizacaoUuid)` antes das consultas. O relatório de pessoas também filtra no banco pela organização selecionada.

### 3.5 REST operacional sem isolamento fim a fim — 🔴 BLOQUEANTE

Foram encontrados controllers REST que chamam diretamente Application Services sem `SegurancaOrganizacaoService`; a maioria desses services também não possui a dependência de segurança. Exemplos concretos:

- `CatequeseRestController` → `CatequeseApplicationService`
- `TurmaCatequeseRestController` → `TurmaCatequeseApplicationService`
- `EtapaCatequeseRestController`
- `InscricaoCatequeseRestController`
- `CatequistaTurmaRestController`
- `SacramentoRestController` e recursos subordinados
- controllers de matrimônio
- controllers financeiros (`ContaFinanceira`, `Contribuicao`, `Dizimista`, `ContaAPagar`, `Pagamento`, `Orcamento`, etc.)
- controllers de projetos/recursos
- controllers de pessoa/família/relacionamentos

**Impacto:** perfil correto + UUID de B pode atravessar o filtro de rota e chegar a um service que busca o recurso globalmente.

**Correção necessária:** autorização organizacional deve estar na camada de caso de uso/Application Service (ou em um componente explícito reutilizado por ela), validando tanto organização atual quanto organização de destino em alterações.

### 3.6 Listagens globais em telas operacionais — 🔴 BLOQUEANTE

Há fluxos web que ainda usam listagens globais, por exemplo:

- `PessoaWebController` → `service.listar()`;
- `SacramentoWebController` → `service.listar()`;
- `FinanceiroWebController` → múltiplos `listar()` globais;
- `VinculoFuncaoWebController` → vínculos globais;
- telas matrimoniais carregam organizações/pessoas globalmente;
- formulários de participantes de sacramento usam `pessoaService.listar()`.

Algumas telas já filtram corretamente pelo conjunto de organizações autorizadas (ex.: organização e vínculo pessoa-organização), mas a estratégia ainda não é uniforme.

**Impacto:** exposição de dados de B na interface de usuário A e possibilidade de usar IDs/UUIDs de B em operações subsequentes.

### 3.7 Pessoas, famílias e relacionamentos — 🔴 BLOQUEANTE / decisão de escopo

`PESSOA` não possui organização própria por decisão de domínio; sua visibilidade deve ser derivada de `VINCULO_PESSOA_ORGANIZACAO_ECLESIAL`. Entretanto, `PessoaApplicationService.listar()` é global e família/relacionamentos também possuem consultas globais.

**Correção necessária:** definir consultas de visibilidade de pessoa baseadas nas organizações autorizadas, sem adicionar `organizacao_id` em PESSOA. Família e relacionamento devem ser visíveis somente quando seus participantes forem visíveis no contexto autorizado, conforme regra a consolidar.

### 3.8 Administração de usuários — 🟠 IMPORTANTE

`/usuarios/**` exige ADMINISTRADOR, porém a tela de criação carrega `PessoaRepository.findAllByOrderByNomeAsc()` e `OrganizacaoEclesialRepository.findAllByOrderByNomeAsc()` diretamente. Como ADMINISTRADOR não é global por definição, a lista de organizações deve ser limitada às organizações do administrador. Pessoas também devem seguir a regra de visibilidade organizacional.

## 4. Matriz de testes A→B

| Fluxo | A→A | A→B | Estado |
|---|---|---|---|
| `SegurancaOrganizacaoService` | coberto | coberto | ⚪ |
| Relatório de contribuições | valida acesso antes da query | teste unitário de negação | ⚪ parcial |
| Demais relatórios/dashboard | validação presente no código | cobertura específica limitada | 🟠 |
| Catequese Web principal | valida atual/destino | cobertura HTTP A→B ausente | 🟠 |
| Catequese REST/sub-recursos | service sem segurança central | não coberto | 🔴 |
| Sacramentos | service sem segurança central | não coberto | 🔴 |
| Matrimônio | service sem segurança central | não coberto | 🔴 |
| Financeiro operacional | services/listagens globais | não coberto | 🔴 |
| Projetos/recursos | services sem segurança central uniforme | não coberto | 🔴 |
| Pessoas/famílias | visibilidade global em pontos | não coberto | 🔴 |
| Administração de usuários | perfil protegido | escopo de org não filtrado na tela | 🟠 |

### Testes obrigatórios antes da v16.10.7

Para cada módulo organizacional essencial:
1. usuário vinculado somente à organização A;
2. criar/consultar/alterar recurso de A → permitido conforme perfil;
3. consultar recurso de B por UUID → `403`/`AccessDeniedException`;
4. alterar recurso de B → negado;
5. mover recurso A→B → negado se B não autorizada;
6. listagem → nenhum registro exclusivo de B;
7. ADMINISTRADOR sem vínculo com B → também negado.

## 5. Arquitetura

### 5.1 Domain — ⚪ SEM ACHADO

Não foram encontrados imports de `application`, `interfaces` ou `infrastructure` dentro de `domain`. O núcleo de domínio permanece independente dessas camadas.

### 5.2 Application → Infrastructure — 🟠 IMPORTANTE

Há Application Services importando diretamente repositórios concretos em `infrastructure.persistence` (11 arquivos detectados). Isso cria acoplamento da camada de casos de uso à implementação de persistência.

**Tratamento MVP:** não é necessário refatorar toda a arquitetura antes do release se isso aumentar risco. Registrar como dívida e, em evolução posterior, mover contratos/ports de repositório para uma camada adequada.

### 5.3 Interfaces — 🟠 IMPORTANTE

Alguns controllers acessam repositories diretamente (ex.: administração de usuários), contornando Application Services e dificultando aplicação uniforme de regras de segurança. Para o release, fluxos sensíveis devem passar por serviços que conheçam o escopo autorizado.

### 5.4 Regras de negócio — ⚪/🟠

As regras funcionais principais auditadas permanecem nos Application Services. O problema predominante não é ausência de regra de domínio, e sim ausência de autorização organizacional transversal nesses casos de uso.

## 6. Dependências e configuração

### 6.1 Java/Spring — ⚪ SEM ACHADO estrutural

`java.version=21`, Spring Boot parent e starters Web, Thymeleaf, JPA, Validation, Security, Actuator e Flyway estão coerentes com o monólito MVC definido.

### 6.2 Testcontainers — ⚪ SEM ACHADO

Dependências PostgreSQL/JUnit estão em `test` e a estratégia é compatível com a validação real de migrations/JPA. O ambiente precisa de Docker, já conhecido no projeto.

### 6.3 Thymeleaf cache — 🟠 IMPORTANTE

`spring.thymeleaf.cache: false` está fixo no `application.yml`. É adequado ao desenvolvimento, mas não ao perfil de produção.

**Correção recomendada:** externalizar (`${THYMELEAF_CACHE:true}`) ou separar perfil dev/prod.

### 6.4 Credenciais PostgreSQL padrão — 🟠 IMPORTANTE

`DB_USERNAME` e `DB_PASSWORD` possuem fallback `postgres`. Isso facilita desenvolvimento local, mas não deve ser aceito silenciosamente como configuração de produção.

**Correção recomendada:** perfil de produção sem senha padrão e secrets obrigatórios no ambiente.

### 6.5 Bootstrap do administrador — ⚪ SEM ACHADO relevante

A senha do primeiro administrador vem de `ADM_ECLESIAL_ADMIN_PASSWORD` e o valor padrão é vazio; não há senha administrativa hardcoded utilizável.

## 7. Banco e Flyway

### 7.1 Estratégia — ⚪ SEM ACHADO

Há migrations V1 a V8, `ddl-auto=validate`, schema `adm_eclesial` e Flyway habilitado. Isso mantém evolução de schema fora do Hibernate.

### 7.2 Segurança — ⚪ SEM ACHADO

V8 cria `usuario_perfil` e `usuario_organizacao`, com FKs, CHECK de perfis e unicidade por usuário/perfil e usuário/organização.

### 7.3 Triggers — ⚪ SEM ACHADO

Não foram introduzidos triggers, preservando a decisão arquitetural do MVP.

## 8. Qualidade para release

### 8.1 Tratamento de erro — ⚪

Configuração do servidor não expõe stack trace, exception, binding errors ou mensagens internas por padrão.

### 8.2 TODO/FIXME — ⚪

A busca textual não encontrou TODO/FIXME/HACK/XXX relevantes em `src/main`/`src/test`.

### 8.3 Cobertura de segurança — 🔴 BLOQUEANTE

Existem 27 classes de teste, mas a cobertura A→B é concentrada no serviço central e em poucos serviços. Falta teste de integração/MockMvc ou service-level por módulo garantindo que o recurso B nunca atravesse a camada de aplicação.

## 9. Correções aplicadas nesta auditoria

Nenhuma correção funcional foi aplicada automaticamente nesta etapa porque o achado bloqueante é transversal. Corrigir apenas controllers isolados daria falsa sensação de segurança e manteria caminhos alternativos abertos. A estratégia segura é centralizar a autorização organizacional nos casos de uso e substituir consultas globais por consultas autorizadas, acompanhadas de testes A→B.

O relatório foi adicionado ao pacote de auditoria para servir de checklist executável da correção.

## 10. Pendências consolidadas

### 🔴 BLOQUEANTES
1. Isolamento organizacional uniforme nos Application Services operacionais.
2. Eliminar listagens globais de dados organizacionais nas telas/endpoints.
3. Definir/implementar visibilidade de PESSOA derivada dos vínculos organizacionais.
4. Cobertura A→B fim a fim para Catequese, Sacramentos, Matrimônio, Financeiro, Projetos e Pessoas.

### 🟠 IMPORTANTES
1. Restringir opções de organização/pessoa na administração de usuários.
2. Reduzir controllers que acessam repositories diretamente.
3. Configuração de produção para cache Thymeleaf.
4. Remover credenciais DB default em perfil de produção.
5. Planejar desacoplamento Application → Infrastructure após estabilização.

### 🟢 PÓS-MVP
1. Refatoração completa para ports/adapters, se desejada.
2. Evoluções de autenticação (MFA/SSO/OAuth2) permanecem fora do MVP.

## 11. Conclusão

A arquitetura funcional e o banco estão suficientemente maduros para continuar a estabilização, mas a auditoria encontrou uma lacuna real entre **autorização por perfil** e **autorização por organização**. O componente de segurança existe e funciona; o problema é que ele ainda não é chamado por todos os casos de uso que precisam dele.

**Decisão técnica:** não promover para v16.10.7 ainda. A próxima correção deve fechar os quatro bloqueantes, executar `mvn clean test` e adicionar testes negativos A→B. Somente após BUILD SUCCESS dessa correção a release MVP deve ser empacotada.

---

## 12. Andamento da correção dos bloqueantes (v16.10.6 em estabilização)

Após a auditoria, foi iniciada a correção transversal sem alteração de schema.

### 12.1 Corrigido nesta etapa

- `SegurancaOrganizacaoService`: suporte explícito a validação por ID interno de organização, mantendo UUID como identificador externo.
- `OrganizacaoEclesialApplicationService`: busca direta validada e listagem limitada às organizações autorizadas.
- `PessoaApplicationService`: visibilidade derivada de `VINCULO_PESSOA_ORGANIZACAO_ECLESIAL`; nenhuma coluna de organização foi adicionada a PESSOA.
- `PessoaRepository`: consultas de visibilidade por conjunto de UUIDs autorizados.
- Catequese: programa, etapa, turma, inscrição e catequista de turma passam a validar a organização proprietária nas entradas públicas; listagens globais de catequese/turma foram filtradas.
- Sacramentos: sacramento, participante e registro sacramental validam a organização do sacramento; listagem principal filtrada.
- Matrimônio: processo, celebração e registro de matrimônio validam a organização do processo/recurso; listagem de processos filtrada.

### 12.2 Ainda bloqueante antes da entrega final da v16.10.6

- Financeiro operacional: aplicar validação por `organizacao_eclesial_id` e filtrar listagens organizacionais.
- Projetos/recursos: aplicar a mesma regra por organização proprietária.
- Famílias/relacionamentos: consolidar visibilidade a partir das pessoas autorizadas.
- Administração de usuários: restringir organizações e pessoas apresentadas ao administrador.
- Testes A→A/A→B dos fluxos corrigidos e dos blocos restantes.

**Status:** esta árvore de trabalho é intermediária e não deve ser promovida para v16.10.7.

---

# Atualização — Correção dos bloqueantes de isolamento (v16.10.6)

## Status após correção

### 🔴 BLOQUEANTE 1 — autorização organizacional inconsistente nos casos de uso
**Status: CORRIGIDO para os fluxos operacionais auditados.**

A validação foi levada para os Application Services/entradas públicas que conhecem a organização proprietária, mantendo o `SecurityFilterChain` como proteção por perfil e `SegurancaOrganizacaoService` como fonte única da autorização organizacional. Foi acrescentada `validarAcessoId(Long)` para entidades financeiras/projetos que armazenam apenas a FK interna BIGINT; o ID é resolvido para o UUID da organização antes da autorização.

Abrangência revisada/corrigida: Organização e vínculos, Pessoa, Catequese e subordinados, Sacramentos, Matrimônio, Financeiro, Repasses, Orçamento, Projetos/Obras, Recursos Vinculados e administração de usuários.

### 🔴 BLOQUEANTE 2 — listagens operacionais globais
**Status: CORRIGIDO nos dados organizacionais auditados.**

Listagens organizacionais agora são limitadas às organizações autorizadas. Onde o repository ainda não oferece consulta por conjunto de organizações, a filtragem é realizada no Application Service. Essa abordagem é segura para o MVP, embora consultas SQL específicas sejam recomendadas posteriormente por eficiência.

Catálogos deliberadamente globais permanecem globais: `FUNCAO_ECLESIAL`, `CATEGORIA_FINANCEIRA` e `FORNECEDOR`, pois o modelo atual não os associa a uma organização.

### 🔴 BLOQUEANTE 3 — visibilidade de PESSOA
**Status: CORRIGIDO sem alterar o modelo.**

`PESSOA` continua sem `organizacao_id`. A visibilidade é derivada de `VINCULO_PESSOA_ORGANIZACAO_ECLESIAL`, por `PessoaRepository.findVisiveis/isVisivel`. Membros de família e relacionamentos entre pessoas também validam a visibilidade das pessoas envolvidas.

**Limitação conhecida — 🟠 IMPORTANTE:** `FAMILIA` não possui organização e pode existir sem membros. Uma família vazia não tem informação suficiente no modelo para determinar organização proprietária. Não foi adicionada FK artificial. O isolamento passa a ser determinável quando existem membros. Recomenda-se decidir pós-MVP se família deve continuar como cadastro global leve ou receber contexto organizacional explícito no caso de uso.

### 🔴 BLOQUEANTE 4 — cobertura A→A / A→B insuficiente
**Status: AMPLIADO; checkpoint Maven ainda obrigatório.**

Foram adicionados testes de isolamento para Conta Financeira e Projeto cobrindo:
- tentativa de criação em organização B → `AccessDeniedException`;
- listagem com registros A e B → somente A é retornado.

Os testes existentes de `SegurancaOrganizacaoService`, relatórios, dashboard e módulos de domínio continuam compondo a suíte. O ambiente de geração deste pacote não possui Maven instalado; portanto o resultado definitivo deve ser validado com `mvn clean test` no ambiente do projeto.

## Administração de usuários

Um `ADMINISTRADOR` permanece limitado às organizações atribuídas. Criação/alteração de usuário valida as organizações solicitadas; edição/consulta de usuário é negada quando o alvo possui organização fora do conjunto administrável. Isso evita escalada A→B via manutenção de usuários.

## Repasses para Cúria

A autorização da regra/obrigação de repasse é baseada na **organização de origem**. Não é exigido que o operador financeiro da paróquia possua acesso administrativo à Cúria de destino, preservando o fluxo legítimo de repasse.

## Banco / Flyway

Nenhuma migration foi criada. Não houve nova tabela, coluna, trigger ou alteração no domínio persistente. A correção é de autorização, consultas e testes.

## Arquitetura e dependências — conclusão

- ⚪ `domain` permanece sem dependência de interfaces/application/infrastructure.
- 🟠 `application` ainda conhece repositories concretos de `infrastructure`; aceito no MVP, candidato a ports/adapters pós-MVP.
- 🟠 filtros de algumas listagens são feitos em memória para reutilizar repositories atuais; seguro funcionalmente, otimização SQL recomendada após release.
- 🟠 `spring.thymeleaf.cache=false` e defaults locais de banco devem ser separados por profile/configuração de produção antes de implantação produtiva.
- ⚪ sem nova expansão funcional do MVP.

## Gate para v16.10.7

A v16.10.6 somente deve ser declarada estável após:

```bash
mvn clean test
```

Se houver `BUILD SUCCESS`, os bloqueantes de código desta auditoria podem ser considerados fechados para preparação da v16.10.7, permanecendo as observações 🟠 acima documentadas como riscos não bloqueantes/pós-MVP.
