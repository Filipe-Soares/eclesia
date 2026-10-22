SET search_path TO adm_eclesial, public;

CREATE TABLE projeto (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, uuid UUID NOT NULL UNIQUE,
 organizacao_eclesial_id BIGINT NOT NULL, item_orcamento_id BIGINT, tipo VARCHAR(20) NOT NULL, status VARCHAR(20) NOT NULL DEFAULT 'PLANEJADO',
 nome VARCHAR(200) NOT NULL, descricao TEXT, data_inicio DATE, data_fim DATE, valor_estimado NUMERIC(15,2),
 CONSTRAINT fk_projeto_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT fk_projeto_item_orcamento FOREIGN KEY (item_orcamento_id) REFERENCES item_orcamento(id),
 CONSTRAINT ck_projeto_tipo CHECK (tipo IN ('PROJETO','OBRA','PASTORAL','SOCIAL','OUTRO')),
 CONSTRAINT ck_projeto_status CHECK (status IN ('PLANEJADO','EM_EXECUCAO','CONCLUIDO','CANCELADO')),
 CONSTRAINT ck_projeto_periodo CHECK (data_fim IS NULL OR data_inicio IS NULL OR data_fim >= data_inicio),
 CONSTRAINT ck_projeto_valor CHECK (valor_estimado IS NULL OR valor_estimado >= 0)
);
CREATE INDEX idx_projeto_org ON projeto(organizacao_eclesial_id);
CREATE INDEX idx_projeto_status ON projeto(status);
CREATE INDEX idx_projeto_item_orcamento ON projeto(item_orcamento_id);

CREATE TABLE recurso_vinculado (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, uuid UUID NOT NULL UNIQUE, organizacao_eclesial_id BIGINT NOT NULL,
 destinacao_contribuicao_id BIGINT NOT NULL UNIQUE, finalidade VARCHAR(500) NOT NULL, valor_original NUMERIC(15,2) NOT NULL,
 status VARCHAR(30) NOT NULL DEFAULT 'DISPONIVEL', data_criacao DATE NOT NULL DEFAULT CURRENT_DATE, observacao TEXT,
 CONSTRAINT fk_recurso_vinculado_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT fk_recurso_vinculado_destinacao FOREIGN KEY (destinacao_contribuicao_id) REFERENCES destinacao_contribuicao(id),
 CONSTRAINT ck_recurso_vinculado_status CHECK (status IN ('DISPONIVEL','PARCIALMENTE_UTILIZADO','UTILIZADO','CANCELADO')),
 CONSTRAINT ck_recurso_vinculado_valor CHECK (valor_original > 0)
);
CREATE INDEX idx_recurso_vinculado_org ON recurso_vinculado(organizacao_eclesial_id);
CREATE INDEX idx_recurso_vinculado_status ON recurso_vinculado(status);

CREATE TABLE fonte_recurso_projeto (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, uuid UUID NOT NULL UNIQUE, projeto_id BIGINT NOT NULL, recurso_vinculado_id BIGINT NOT NULL,
 valor_alocado NUMERIC(15,2) NOT NULL, descricao VARCHAR(500),
 CONSTRAINT fk_fonte_recurso_projeto FOREIGN KEY (projeto_id) REFERENCES projeto(id) ON DELETE CASCADE,
 CONSTRAINT fk_fonte_recurso_recurso FOREIGN KEY (recurso_vinculado_id) REFERENCES recurso_vinculado(id),
 CONSTRAINT ck_fonte_recurso_valor CHECK (valor_alocado > 0),
 CONSTRAINT uq_fonte_recurso_projeto_recurso UNIQUE (projeto_id,recurso_vinculado_id)
);
CREATE INDEX idx_fonte_recurso_projeto ON fonte_recurso_projeto(projeto_id);
CREATE INDEX idx_fonte_recurso_recurso ON fonte_recurso_projeto(recurso_vinculado_id);

CREATE TABLE utilizacao_recurso (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, uuid UUID NOT NULL UNIQUE, fonte_recurso_projeto_id BIGINT NOT NULL, movimentacao_financeira_id BIGINT NOT NULL,
 valor NUMERIC(15,2) NOT NULL, descricao VARCHAR(500),
 CONSTRAINT fk_utilizacao_recurso_fonte FOREIGN KEY (fonte_recurso_projeto_id) REFERENCES fonte_recurso_projeto(id),
 CONSTRAINT fk_utilizacao_recurso_mov FOREIGN KEY (movimentacao_financeira_id) REFERENCES movimentacao_financeira(id),
 CONSTRAINT ck_utilizacao_recurso_valor CHECK (valor > 0),
 CONSTRAINT uq_utilizacao_recurso_fonte_mov UNIQUE (fonte_recurso_projeto_id,movimentacao_financeira_id)
);
CREATE INDEX idx_utilizacao_recurso_fonte ON utilizacao_recurso(fonte_recurso_projeto_id);
CREATE INDEX idx_utilizacao_recurso_mov ON utilizacao_recurso(movimentacao_financeira_id);
