CREATE INDEX IF NOT EXISTS idx_processo_matrimonial_org ON processo_matrimonial(organizacao_eclesial_id);
CREATE INDEX IF NOT EXISTS idx_processo_matrimonial_nubente1 ON processo_matrimonial(nubente_1_id);
CREATE INDEX IF NOT EXISTS idx_processo_matrimonial_nubente2 ON processo_matrimonial(nubente_2_id);
CREATE INDEX IF NOT EXISTS idx_celebracao_matrimonial_data ON celebracao_matrimonial(data_celebracao);
