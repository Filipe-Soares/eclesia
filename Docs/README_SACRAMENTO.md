# Módulo de Sacramentos

## Escopo MVP
O módulo registra a celebração sacramental, seus participantes e o registro canônico/livro.

### Modelo
- `SACRAMENTO`
- `PARTICIPANTE_SACRAMENTO`
- `REGISTRO_SACRAMENTAL`

`MATRIMONIO` permanece como tipo de sacramento, mas o processo matrimonial detalhado continua sendo tratado no módulo específico de Matrimônio.

### Tipos
- BATISMO
- EUCARISTIA
- CRISMA
- MATRIMONIO
- ORDEM
- UNCAO_ENFERMOS

### Regras principais
- Organização precisa existir e estar ativa.
- Pessoa participante precisa existir e estar ativa.
- Sacramento cancelado não aceita novos participantes nem alteração do registro sacramental.
- Uma pessoa não pode receber a mesma participação duas vezes no mesmo sacramento.
- Registro sacramental é 1:0..1 por sacramento e é salvo por operação idempotente de `PUT`.
- Datas são `LocalDate`; identificadores externos são UUID.

## REST
- `POST /api/v1/sacramentos`
- `PUT /api/v1/sacramentos/{uuid}`
- `GET /api/v1/sacramentos`
- `GET /api/v1/sacramentos/{uuid}`
- `GET /api/v1/sacramentos/por-organizacao/{organizacaoUuid}`
- `POST /api/v1/participantes-sacramento`
- `GET /api/v1/participantes-sacramento/{uuid}`
- `GET /api/v1/participantes-sacramento/por-sacramento/{sacramentoUuid}`
- `PUT /api/v1/registros-sacramentais`
- `GET /api/v1/registros-sacramentais/por-sacramento/{sacramentoUuid}`
- `GET /api/v1/registros-sacramentais/{uuid}`

## Thymeleaf
- `/sacramentos`
- `/sacramentos/novo`
- `/sacramentos/{uuid}`
- `/sacramentos/{uuid}/editar`
- `/sacramentos/{uuid}/participantes`
- `/sacramentos/{uuid}/participantes/novo`
- `/sacramentos/{uuid}/registro`
