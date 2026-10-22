# ADM Eclesial — v16.10.7 Release MVP

Esta versão é o marco de release do MVP.

## Baseline técnica
O código-fonte desta release parte da base validada da v16.10.6.1 e incorpora as mudanças pós-baseline (edição de cadastros principais e modernização da UI financeira). A suíte de testes atual foi validada com:

- `mvn clean test`
- 136 testes
- 0 falhas
- 0 erros
- BUILD SUCCESS

Esta release (v16.10.7) é o fechamento documental/empacotamento do MVP e reflete o estado atual do código, incluindo as mudanças pós-baseline.

## Stack
- Java 21
- Spring Boot 3
- Maven
- PostgreSQL
- Flyway
- Spring Security
- Thymeleaf
- Testcontainers nos testes de integração

## Execução
1. Configure PostgreSQL e as variáveis de ambiente do projeto.
2. Para o primeiro administrador, defina `ADM_ECLESIAL_ADMIN_PASSWORD`.
3. Opcionalmente defina `ADM_ECLESIAL_ADMIN_LOGIN`.
4. Execute `mvn clean test`.
5. Execute a aplicação conforme o fluxo Maven/Spring Boot do projeto.

## Segurança
Os perfis do MVP são:
- ADMINISTRADOR
- SECRETARIA
- CATEQUESE
- FINANCEIRO
- CONSULTA

Perfis de acesso são um conceito de segurança e não substituem `FUNCAO_ECLESIAL`.

ADMINISTRADOR não possui acesso global implícito: continua limitado às organizações atribuídas.

## Banco
Flyway é responsável pela evolução do schema. A aplicação utiliza `ddl-auto=validate`.
Não foram adicionadas migrations na v16.10.7.

## Produção
Não utilize credenciais locais/default como credenciais de produção.
Forneça banco, usuário, senha e credenciais administrativas através da configuração externa adequada ao ambiente.
Mantenha CSRF e os controles de sessão/segurança habilitados.

## Limitações conhecidas
`FAMILIA` pode existir sem membros e não possui organização proprietária própria por decisão do modelo.
Catálogos globais permanecem globais quando não possuem vínculo organizacional.

## Pós-MVP
Permanecem fora deste release, entre outros:
OAuth2/MFA/SSO/ACL, conciliação bancária, contabilidade e fechamento contábil avançados,
controle patrimonial, folha, estoque, integrações bancárias e frequência/avaliação detalhada da catequese.

Consulte também os relatórios e READMEs técnicos mantidos no pacote.
