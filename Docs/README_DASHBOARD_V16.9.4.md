# ADM Eclesial v16.9.4 — Dashboard CATEQUESE

## Escopo
- Visão exclusiva do perfil CATEQUESE em `/dashboard`.
- Organização limitada às organizações autorizadas do usuário.
- Período padrão continua sendo o mês atual.
- Sem dados financeiros.

## Indicadores
- Turmas ativas.
- Inscritos ativos em turmas ativas.
- Vagas disponíveis, derivadas de `capacidade - inscrições ativas`, nunca negativas.
- Turmas ativas sem catequista principal.

## Alertas
- Turmas ativas sem catequista principal.
- Turmas ativas sem vagas disponíveis (somente quando a capacidade está definida).

## Persistência
Nenhuma migration e nenhuma tabela de dashboard foram adicionadas. Todos os valores são derivados das estruturas existentes.
