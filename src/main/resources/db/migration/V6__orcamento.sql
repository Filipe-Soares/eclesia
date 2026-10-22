SET search_path TO adm_eclesial, public;

CREATE TABLE orcamento (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE,
 organizacao_eclesial_id BIGINT NOT NULL,
 ano INTEGER NOT NULL,
 nome VARCHAR(200) NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'PLANEJADO',
 data_aprovacao DATE,
 observacao TEXT,
 CONSTRAINT fk_orcamento_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT ck_orcamento_ano CHECK (ano BETWEEN 2000 AND 2200),
 CONSTRAINT ck_orcamento_status CHECK (status IN ('PLANEJADO','APROVADO','ENCERRADO','CANCELADO'))
);
CREATE UNIQUE INDEX uq_orcamento_org_ano ON orcamento(organizacao_eclesial_id,ano);
CREATE INDEX idx_orcamento_org ON orcamento(organizacao_eclesial_id);

CREATE TABLE item_orcamento (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE,
 orcamento_id BIGINT NOT NULL,
 categoria_financeira_id BIGINT NOT NULL,
 centro_custo_id BIGINT,
 descricao VARCHAR(300) NOT NULL,
 valor_planejado NUMERIC(15,2) NOT NULL,
 CONSTRAINT fk_item_orcamento_orcamento FOREIGN KEY (orcamento_id) REFERENCES orcamento(id) ON DELETE CASCADE,
 CONSTRAINT fk_item_orcamento_categoria FOREIGN KEY (categoria_financeira_id) REFERENCES categoria_financeira(id),
 CONSTRAINT fk_item_orcamento_centro FOREIGN KEY (centro_custo_id) REFERENCES centro_custo(id),
 CONSTRAINT ck_item_orcamento_valor CHECK (valor_planejado > 0)
);
CREATE INDEX idx_item_orcamento_orcamento ON item_orcamento(orcamento_id);
CREATE INDEX idx_item_orcamento_categoria ON item_orcamento(categoria_financeira_id);
CREATE INDEX idx_item_orcamento_centro ON item_orcamento(centro_custo_id);
