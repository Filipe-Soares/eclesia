-- Ajustes do módulo de Catequese para o modelo MVP consolidado.
-- A V1 já cria as cinco tabelas de Catequese; esta migration corrige
-- decisões antigas sem recriar tabelas.

SET search_path TO adm_eclesial, public;

-- CATEQUESE: observação opcional.
ALTER TABLE catequese
    ADD COLUMN observacao TEXT;

-- ETAPA: nome único dentro da catequese.
ALTER TABLE etapa_catequese
    ADD CONSTRAINT uk_etapa_catequese_nome UNIQUE (catequese_id, nome);

-- TURMA: informações operacionais da turma.
ALTER TABLE turma_catequese
    ADD COLUMN capacidade INTEGER,
    ADD COLUMN local VARCHAR(255),
    ADD COLUMN observacao TEXT;

ALTER TABLE turma_catequese
    ADD CONSTRAINT ck_turma_catequese_capacidade
        CHECK (capacidade IS NULL OR capacidade > 0),
    ADD CONSTRAINT ck_turma_catequese_turno
        CHECK (turno IS NULL OR turno IN ('MANHA', 'TARDE', 'NOITE', 'INTEGRAL', 'OUTRO'));

CREATE INDEX idx_turma_catequese_organizacao
    ON turma_catequese (organizacao_eclesial_id);

CREATE INDEX idx_turma_catequese_ano
    ON turma_catequese (ano);

-- INSCRICAO: Família não faz parte do processo de matrícula do MVP.
ALTER TABLE inscricao_catequese
    DROP CONSTRAINT IF EXISTS fk_inscricao_familia;

ALTER TABLE inscricao_catequese
    DROP COLUMN familia_id;

CREATE UNIQUE INDEX uq_inscricao_catequese_pessoa_turma_ativa
    ON inscricao_catequese (turma_catequese_id, pessoa_id)
    WHERE status = 'ATIVA';

-- CATEQUISTA_TURMA: informações do vínculo do catequista com a turma.
ALTER TABLE catequista_turma
    ADD COLUMN observacao TEXT;

-- Índice de apoio para consultas por catequista.
CREATE INDEX idx_catequista_turma_pessoa
    ON catequista_turma (pessoa_id);
