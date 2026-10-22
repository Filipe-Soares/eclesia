# ADM Eclesial v16.8.2 — Relatório de Sacramentos

- Rota: `/relatorios/sacramentos`
- Aba padrão: **Registros** (`visao=registros`)
- Segunda aba: **Por Pessoa** (`visao=pessoas`)
- Filtros: organização, tipo de sacramento, período e pessoa.
- Registros: uma linha por Sacramento, agregando SACRAMENTANDO/CONJUGE como pessoa principal e exibindo dados de RegistroSacramental quando existentes.
- Por Pessoa: histórico encontrado no sistema para participantes SACRAMENTANDO/CONJUGE. Ausência não é interpretada como pendência sacramental.
- Segurança: organização obrigatória e validada por `SegurancaOrganizacaoService`.
- Perfis de leitura: ADMINISTRADOR, SECRETARIA, CATEQUESE e CONSULTA. FINANCEIRO não recebe acesso ao relatório sacramental.
- Nenhuma migration e nenhuma tabela nova.

Validação local: `mvn clean test`.
