-- Integração Matrimonio <-> Sacramento.
-- O Sacramento de MATRIMONIO nasce exclusivamente pelo fluxo matrimonial.
SET search_path TO adm_eclesial, public;

ALTER TABLE matrimonio
    ADD COLUMN sacramento_id BIGINT;

ALTER TABLE matrimonio
    ADD CONSTRAINT uq_matrimonio_sacramento UNIQUE (sacramento_id);

ALTER TABLE matrimonio
    ADD CONSTRAINT fk_matrimonio_sacramento
    FOREIGN KEY (sacramento_id) REFERENCES sacramento(id);

