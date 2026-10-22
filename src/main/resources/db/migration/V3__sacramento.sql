-- Ajustes do módulo de Sacramentos: domínio, integridade e índices.
ALTER TABLE participante_sacramento DROP CONSTRAINT IF EXISTS ck_participante_sacramento_tipo;
ALTER TABLE participante_sacramento ADD CONSTRAINT ck_participante_sacramento_tipo CHECK (tipo_participacao IN ('SACRAMENTANDO','MINISTRO','PADRINHO','MADRINHA','PAI','MAE','CONJUGE','TESTEMUNHA','OUTRO'));
CREATE UNIQUE INDEX IF NOT EXISTS uq_participante_sacramento_pessoa_tipo ON participante_sacramento(sacramento_id,pessoa_id,tipo_participacao);
CREATE INDEX IF NOT EXISTS idx_sacramento_org_data ON sacramento(organizacao_eclesial_id,data_celebracao);
CREATE INDEX IF NOT EXISTS idx_sacramento_tipo ON sacramento(tipo);
CREATE INDEX IF NOT EXISTS idx_registro_sacramental_numero ON registro_sacramental(numero_registro);
