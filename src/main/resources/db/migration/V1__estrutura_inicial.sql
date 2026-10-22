-- ADM ECLESIAL - PostgreSQL
-- DER Consolidado v2.0
-- Schema: adm_eclesial
-- PK interna: BIGINT IDENTITY | UUID externo
-- ENUMs de domínio: VARCHAR + CHECK (mapeados para enum Java)
-- Triggers: nenhuma nesta versão

CREATE SCHEMA IF NOT EXISTS adm_eclesial;
SET search_path TO adm_eclesial, public;

CREATE TABLE organizacao_eclesial (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE,
 nome VARCHAR(200) NOT NULL,
 nome_reduzido VARCHAR(100),
 tipo VARCHAR(30) NOT NULL,
 cnpj VARCHAR(14), email VARCHAR(200), telefone VARCHAR(30),
 ativo BOOLEAN NOT NULL DEFAULT TRUE,
 data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 observacao TEXT,
 CONSTRAINT ck_organizacao_tipo CHECK (tipo IN ('DIOCESE','CURIA','PAROQUIA','COMUNIDADE','PASTORAL','CONSELHO','MOVIMENTO','GRUPO','OUTRO')),
 CONSTRAINT ck_organizacao_cnpj CHECK (cnpj IS NULL OR cnpj ~ '^[0-9]{14}$')
);
CREATE INDEX idx_organizacao_eclesial_nome ON organizacao_eclesial (LOWER(nome));

CREATE TABLE pessoa (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE,
 nome VARCHAR(200) NOT NULL, nome_social VARCHAR(200), data_nascimento DATE,
 sexo VARCHAR(20), cpf VARCHAR(11), rg VARCHAR(30), email VARCHAR(200),
 observacao TEXT, ativo BOOLEAN NOT NULL DEFAULT TRUE,
 data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_pessoa_sexo CHECK (sexo IS NULL OR sexo IN ('MASCULINO','FEMININO','NAO_INFORMADO')),
 CONSTRAINT ck_pessoa_cpf CHECK (cpf IS NULL OR cpf ~ '^[0-9]{11}$')
);
CREATE UNIQUE INDEX uq_pessoa_cpf ON pessoa(cpf) WHERE cpf IS NOT NULL;
CREATE INDEX idx_pessoa_nome ON pessoa(LOWER(nome));

CREATE TABLE vinculo_organizacao_eclesial (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE,
 organizacao_pai_id BIGINT NOT NULL,
 organizacao_filha_id BIGINT NOT NULL,
 tipo_vinculo VARCHAR(20) NOT NULL,
 data_inicio DATE NOT NULL, data_fim DATE,
 ativo BOOLEAN NOT NULL DEFAULT TRUE, observacao TEXT,
 CONSTRAINT fk_vinc_org_pai FOREIGN KEY (organizacao_pai_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT fk_vinc_org_filha FOREIGN KEY (organizacao_filha_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT ck_vinc_org_nao_auto CHECK (organizacao_pai_id <> organizacao_filha_id),
 CONSTRAINT ck_vinc_org_tipo CHECK (tipo_vinculo IN ('HIERARQUICO','PASTORAL','ADMINISTRATIVO','VINCULACAO')),
 CONSTRAINT ck_vinc_org_periodo CHECK (data_fim IS NULL OR data_fim >= data_inicio)
);
CREATE INDEX idx_vinc_org_pai ON vinculo_organizacao_eclesial(organizacao_pai_id);
CREATE INDEX idx_vinc_org_filha ON vinculo_organizacao_eclesial(organizacao_filha_id);

CREATE TABLE endereco (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, pessoa_id BIGINT NOT NULL, tipo VARCHAR(20) NOT NULL,
 logradouro VARCHAR(200) NOT NULL, numero VARCHAR(30), complemento VARCHAR(150),
 bairro VARCHAR(100), cidade VARCHAR(100) NOT NULL, uf CHAR(2) NOT NULL, cep VARCHAR(8),
 principal BOOLEAN NOT NULL DEFAULT FALSE,
 CONSTRAINT fk_endereco_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id) ON DELETE CASCADE,
 CONSTRAINT ck_endereco_tipo CHECK (tipo IN ('RESIDENCIAL','COMERCIAL','OUTRO')),
 CONSTRAINT ck_endereco_uf CHECK (uf ~ '^[A-Z]{2}$'),
 CONSTRAINT ck_endereco_cep CHECK (cep IS NULL OR cep ~ '^[0-9]{8}$')
);
CREATE INDEX idx_endereco_pessoa ON endereco(pessoa_id);
CREATE UNIQUE INDEX uq_endereco_principal ON endereco(pessoa_id) WHERE principal;

CREATE TABLE contato (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, pessoa_id BIGINT NOT NULL,
 tipo VARCHAR(20) NOT NULL, valor VARCHAR(200) NOT NULL,
 principal BOOLEAN NOT NULL DEFAULT FALSE, ativo BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT fk_contato_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id) ON DELETE CASCADE,
 CONSTRAINT ck_contato_tipo CHECK (tipo IN ('CELULAR','TELEFONE','EMAIL','WHATSAPP','OUTRO'))
);
CREATE INDEX idx_contato_pessoa ON contato(pessoa_id);

CREATE TABLE usuario (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, pessoa_id BIGINT NOT NULL UNIQUE,
 login VARCHAR(100) NOT NULL UNIQUE, senha_hash VARCHAR(255) NOT NULL,
 ativo BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT fk_usuario_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id)
);

CREATE TABLE familia (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, nome VARCHAR(200) NOT NULL,
 ativo BOOLEAN NOT NULL DEFAULT TRUE,
 data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE membro_familia (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, familia_id BIGINT NOT NULL, pessoa_id BIGINT NOT NULL,
 tipo VARCHAR(20) NOT NULL, data_inicio DATE NOT NULL, data_fim DATE,
 CONSTRAINT fk_membro_familia_familia FOREIGN KEY (familia_id) REFERENCES familia(id) ON DELETE CASCADE,
 CONSTRAINT fk_membro_familia_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id),
 CONSTRAINT ck_membro_familia_tipo CHECK (tipo IN ('RESPONSAVEL','CONJUGE','FILHO','OUTRO')),
 CONSTRAINT ck_membro_familia_periodo CHECK (data_fim IS NULL OR data_fim >= data_inicio)
);
CREATE UNIQUE INDEX uq_membro_familia_ativo ON membro_familia(familia_id,pessoa_id) WHERE data_fim IS NULL;
CREATE INDEX idx_membro_familia_pessoa ON membro_familia(pessoa_id);

CREATE TABLE relacionamento_pessoa (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, pessoa_origem_id BIGINT NOT NULL, pessoa_destino_id BIGINT NOT NULL,
 tipo_relacionamento VARCHAR(20) NOT NULL, data_inicio DATE NOT NULL, data_fim DATE, observacao TEXT,
 CONSTRAINT fk_rel_pessoa_origem FOREIGN KEY (pessoa_origem_id) REFERENCES pessoa(id),
 CONSTRAINT fk_rel_pessoa_destino FOREIGN KEY (pessoa_destino_id) REFERENCES pessoa(id),
 CONSTRAINT ck_rel_pessoa_nao_auto CHECK (pessoa_origem_id <> pessoa_destino_id),
 CONSTRAINT ck_rel_pessoa_tipo CHECK (tipo_relacionamento IN ('PAI','MAE','FILHO','FILHA','CONJUGE','IRMAO','IRMA','AVO','AVA','NETO','NETA','OUTRO')),
 CONSTRAINT ck_rel_pessoa_periodo CHECK (data_fim IS NULL OR data_fim >= data_inicio)
);
CREATE INDEX idx_rel_pessoa_origem ON relacionamento_pessoa(pessoa_origem_id);
CREATE INDEX idx_rel_pessoa_destino ON relacionamento_pessoa(pessoa_destino_id);

CREATE TABLE vinculo_pessoa_organizacao_eclesial (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, pessoa_id BIGINT NOT NULL, organizacao_eclesial_id BIGINT NOT NULL,
 data_inicio DATE NOT NULL, data_fim DATE,
 status VARCHAR(20) NOT NULL DEFAULT 'ATIVO', principal BOOLEAN NOT NULL DEFAULT FALSE, observacao TEXT,
 CONSTRAINT fk_vinc_pessoa_org_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id),
 CONSTRAINT fk_vinc_pessoa_org_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT ck_vinc_pessoa_org_status CHECK (status IN ('ATIVO','INATIVO','SUSPENSO')),
 CONSTRAINT ck_vinc_pessoa_org_periodo CHECK (data_fim IS NULL OR data_fim >= data_inicio)
);
CREATE INDEX idx_vinc_pessoa_org_pessoa ON vinculo_pessoa_organizacao_eclesial(pessoa_id);
CREATE INDEX idx_vinc_pessoa_org_org ON vinculo_pessoa_organizacao_eclesial(organizacao_eclesial_id);
CREATE UNIQUE INDEX uq_vinc_pessoa_org_principal_ativo ON vinculo_pessoa_organizacao_eclesial(pessoa_id, organizacao_eclesial_id) WHERE principal AND data_fim IS NULL AND status = 'ATIVO';

CREATE TABLE funcao_eclesial (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, codigo VARCHAR(50) NOT NULL UNIQUE,
 nome VARCHAR(150) NOT NULL, descricao TEXT, ativo BOOLEAN NOT NULL DEFAULT TRUE,
 exclusivo_na_organizacao BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE vinculo_funcao_eclesial (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, vinculo_pessoa_organizacao_id BIGINT NOT NULL,
 funcao_eclesial_id BIGINT NOT NULL, data_inicio DATE NOT NULL, data_fim DATE,
 principal BOOLEAN NOT NULL DEFAULT FALSE, observacao TEXT,
 CONSTRAINT fk_vinc_funcao_vinculo FOREIGN KEY (vinculo_pessoa_organizacao_id) REFERENCES vinculo_pessoa_organizacao_eclesial(id) ON DELETE CASCADE,
 CONSTRAINT fk_vinc_funcao_funcao FOREIGN KEY (funcao_eclesial_id) REFERENCES funcao_eclesial(id),
 CONSTRAINT ck_vinc_funcao_periodo CHECK (data_fim IS NULL OR data_fim >= data_inicio)
);
CREATE INDEX idx_vinc_funcao_vinculo ON vinculo_funcao_eclesial(vinculo_pessoa_organizacao_id);
CREATE INDEX idx_vinc_funcao_funcao ON vinculo_funcao_eclesial(funcao_eclesial_id);
CREATE UNIQUE INDEX uq_vinc_funcao_principal_aberta ON vinculo_funcao_eclesial(vinculo_pessoa_organizacao_id) WHERE principal AND data_fim IS NULL;

-- CATEQUESE
CREATE TABLE catequese (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, organizacao_eclesial_id BIGINT NOT NULL,
 nome VARCHAR(200) NOT NULL, descricao TEXT, status VARCHAR(20) NOT NULL DEFAULT 'PLANEJADA',
 data_inicio DATE, data_fim DATE,
 CONSTRAINT fk_catequese_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT ck_catequese_status CHECK (status IN ('PLANEJADA','ATIVA','ENCERRADA','CANCELADA')),
 CONSTRAINT ck_catequese_periodo CHECK (data_fim IS NULL OR data_inicio IS NULL OR data_fim >= data_inicio)
);

CREATE TABLE etapa_catequese (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, catequese_id BIGINT NOT NULL,
 nome VARCHAR(150) NOT NULL, descricao TEXT, ordem INTEGER NOT NULL, ativo BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT fk_etapa_catequese FOREIGN KEY (catequese_id) REFERENCES catequese(id) ON DELETE CASCADE,
 CONSTRAINT ck_etapa_ordem CHECK (ordem > 0)
);
CREATE UNIQUE INDEX uq_etapa_catequese_ordem ON etapa_catequese(catequese_id,ordem);

CREATE TABLE turma_catequese (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, etapa_catequese_id BIGINT NOT NULL, organizacao_eclesial_id BIGINT NOT NULL,
 nome VARCHAR(150) NOT NULL, ano INTEGER, turno VARCHAR(30), data_inicio DATE, data_fim DATE,
 status VARCHAR(20) NOT NULL DEFAULT 'PLANEJADA',
 CONSTRAINT fk_turma_etapa FOREIGN KEY (etapa_catequese_id) REFERENCES etapa_catequese(id),
 CONSTRAINT fk_turma_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT ck_turma_status CHECK (status IN ('PLANEJADA','ATIVA','ENCERRADA','CANCELADA')),
 CONSTRAINT ck_turma_ano CHECK (ano IS NULL OR ano BETWEEN 2000 AND 2200),
 CONSTRAINT ck_turma_periodo CHECK (data_fim IS NULL OR data_inicio IS NULL OR data_fim >= data_inicio)
);

CREATE TABLE inscricao_catequese (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, turma_catequese_id BIGINT NOT NULL, pessoa_id BIGINT NOT NULL,
 familia_id BIGINT, data_inscricao DATE NOT NULL DEFAULT CURRENT_DATE,
 status VARCHAR(20) NOT NULL DEFAULT 'ATIVA', observacao TEXT,
 CONSTRAINT fk_inscricao_turma FOREIGN KEY (turma_catequese_id) REFERENCES turma_catequese(id),
 CONSTRAINT fk_inscricao_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id),
 CONSTRAINT fk_inscricao_familia FOREIGN KEY (familia_id) REFERENCES familia(id),
 CONSTRAINT ck_inscricao_status CHECK (status IN ('ATIVA','CANCELADA','CONCLUIDA','TRANSFERIDA'))
);
CREATE INDEX idx_inscricao_turma ON inscricao_catequese(turma_catequese_id);
CREATE INDEX idx_inscricao_pessoa ON inscricao_catequese(pessoa_id);

CREATE TABLE catequista_turma (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, turma_catequese_id BIGINT NOT NULL, pessoa_id BIGINT NOT NULL,
 principal BOOLEAN NOT NULL DEFAULT FALSE, data_inicio DATE NOT NULL, data_fim DATE,
 CONSTRAINT fk_catequista_turma_turma FOREIGN KEY (turma_catequese_id) REFERENCES turma_catequese(id) ON DELETE CASCADE,
 CONSTRAINT fk_catequista_turma_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id),
 CONSTRAINT ck_catequista_turma_periodo CHECK (data_fim IS NULL OR data_fim >= data_inicio)
);
CREATE UNIQUE INDEX uq_catequista_turma_principal ON catequista_turma(turma_catequese_id) WHERE principal AND data_fim IS NULL;

-- SACRAMENTOS
CREATE TABLE sacramento (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, organizacao_eclesial_id BIGINT NOT NULL,
 tipo VARCHAR(30) NOT NULL, data_celebracao DATE NOT NULL, local VARCHAR(200),
 status VARCHAR(20) NOT NULL DEFAULT 'REGISTRADO', observacao TEXT,
 data_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_sacramento_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT ck_sacramento_tipo CHECK (tipo IN ('BATISMO','EUCARISTIA','CRISMA','MATRIMONIO','ORDEM','UNCAO_ENFERMOS')),
 CONSTRAINT ck_sacramento_status CHECK (status IN ('REGISTRADO','CANCELADO'))
);

CREATE TABLE participante_sacramento (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, sacramento_id BIGINT NOT NULL, pessoa_id BIGINT NOT NULL,
 tipo_participacao VARCHAR(50) NOT NULL, observacao TEXT,
 CONSTRAINT fk_part_sacramento FOREIGN KEY (sacramento_id) REFERENCES sacramento(id) ON DELETE CASCADE,
 CONSTRAINT fk_part_sacramento_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id)
);
CREATE INDEX idx_part_sacramento ON participante_sacramento(sacramento_id);
CREATE INDEX idx_part_sacramento_pessoa ON participante_sacramento(pessoa_id);

CREATE TABLE registro_sacramental (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, sacramento_id BIGINT NOT NULL UNIQUE,
 livro VARCHAR(50), folha VARCHAR(50), termo VARCHAR(50), numero_registro VARCHAR(50), observacao TEXT,
 CONSTRAINT fk_registro_sacramento FOREIGN KEY (sacramento_id) REFERENCES sacramento(id) ON DELETE CASCADE
);

-- MATRIMÔNIO
CREATE TABLE processo_matrimonial (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, organizacao_eclesial_id BIGINT NOT NULL,
 nubente_1_id BIGINT NOT NULL, nubente_2_id BIGINT NOT NULL,
 data_abertura DATE NOT NULL, data_encerramento DATE,
 status VARCHAR(20) NOT NULL DEFAULT 'ABERTO', observacao TEXT,
 CONSTRAINT fk_processo_matrimonial_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT fk_processo_matrimonial_nubente1 FOREIGN KEY (nubente_1_id) REFERENCES pessoa(id),
 CONSTRAINT fk_processo_matrimonial_nubente2 FOREIGN KEY (nubente_2_id) REFERENCES pessoa(id),
 CONSTRAINT ck_processo_matrimonial_nubentes CHECK (nubente_1_id <> nubente_2_id),
 CONSTRAINT ck_processo_matrimonial_status CHECK (status IN ('ABERTO','EM_ANALISE','CONCLUIDO','CANCELADO')),
 CONSTRAINT ck_processo_matrimonial_periodo CHECK (data_encerramento IS NULL OR data_encerramento >= data_abertura)
);

CREATE TABLE celebracao_matrimonial (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, processo_matrimonial_id BIGINT NOT NULL UNIQUE,
 organizacao_eclesial_id BIGINT NOT NULL, celebrante_pessoa_id BIGINT NOT NULL,
 data_celebracao DATE NOT NULL, local VARCHAR(200), observacao TEXT,
 CONSTRAINT fk_celebracao_processo FOREIGN KEY (processo_matrimonial_id) REFERENCES processo_matrimonial(id),
 CONSTRAINT fk_celebracao_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT fk_celebracao_celebrante FOREIGN KEY (celebrante_pessoa_id) REFERENCES pessoa(id)
);

CREATE TABLE matrimonio (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, processo_matrimonial_id BIGINT NOT NULL UNIQUE,
 celebracao_matrimonial_id BIGINT NOT NULL UNIQUE,
 data_registro DATE NOT NULL DEFAULT CURRENT_DATE, observacao TEXT,
 CONSTRAINT fk_matrimonio_processo FOREIGN KEY (processo_matrimonial_id) REFERENCES processo_matrimonial(id),
 CONSTRAINT fk_matrimonio_celebracao FOREIGN KEY (celebracao_matrimonial_id) REFERENCES celebracao_matrimonial(id)
);

-- FINANCEIRO
CREATE TABLE conta_financeira (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, organizacao_eclesial_id BIGINT NOT NULL,
 nome VARCHAR(150) NOT NULL, tipo VARCHAR(20) NOT NULL,
 banco VARCHAR(100), agencia VARCHAR(30), numero VARCHAR(50), ativo BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT fk_conta_financeira_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT ck_conta_financeira_tipo CHECK (tipo IN ('CORRENTE','POUPANCA','CAIXA','APLICACAO','OUTRA'))
);

CREATE TABLE categoria_financeira (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, codigo VARCHAR(50) NOT NULL UNIQUE,
 nome VARCHAR(150) NOT NULL, tipo VARCHAR(30), ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE centro_custo (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, organizacao_eclesial_id BIGINT NOT NULL,
 codigo VARCHAR(50) NOT NULL, nome VARCHAR(150) NOT NULL, ativo BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT fk_centro_custo_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id)
);
CREATE UNIQUE INDEX uq_centro_custo_org_codigo ON centro_custo(organizacao_eclesial_id,codigo);

CREATE TABLE movimentacao_financeira (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, conta_financeira_id BIGINT NOT NULL,
 categoria_financeira_id BIGINT, centro_custo_id BIGINT,
 tipo VARCHAR(20) NOT NULL, valor NUMERIC(15,2) NOT NULL,
 data_movimentacao DATE NOT NULL, competencia DATE, descricao VARCHAR(500),
 status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA',
 CONSTRAINT fk_mov_conta FOREIGN KEY (conta_financeira_id) REFERENCES conta_financeira(id),
 CONSTRAINT fk_mov_categoria FOREIGN KEY (categoria_financeira_id) REFERENCES categoria_financeira(id),
 CONSTRAINT fk_mov_centro_custo FOREIGN KEY (centro_custo_id) REFERENCES centro_custo(id),
 CONSTRAINT ck_mov_tipo CHECK (tipo IN ('ENTRADA','SAIDA','TRANSFERENCIA')),
 CONSTRAINT ck_mov_status CHECK (status IN ('PENDENTE','CONFIRMADA','CANCELADA')),
 CONSTRAINT ck_mov_valor CHECK (valor > 0)
);
CREATE INDEX idx_mov_conta ON movimentacao_financeira(conta_financeira_id);
CREATE INDEX idx_mov_data ON movimentacao_financeira(data_movimentacao);
CREATE INDEX idx_mov_competencia ON movimentacao_financeira(competencia);

CREATE TABLE transferencia_financeira (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, conta_origem_id BIGINT NOT NULL, conta_destino_id BIGINT NOT NULL,
 movimentacao_origem_id BIGINT NOT NULL UNIQUE, movimentacao_destino_id BIGINT NOT NULL UNIQUE,
 valor NUMERIC(15,2) NOT NULL, data_transferencia DATE NOT NULL, observacao TEXT,
 CONSTRAINT fk_transferencia_conta_origem FOREIGN KEY (conta_origem_id) REFERENCES conta_financeira(id),
 CONSTRAINT fk_transferencia_conta_destino FOREIGN KEY (conta_destino_id) REFERENCES conta_financeira(id),
 CONSTRAINT fk_transferencia_mov_origem FOREIGN KEY (movimentacao_origem_id) REFERENCES movimentacao_financeira(id),
 CONSTRAINT fk_transferencia_mov_destino FOREIGN KEY (movimentacao_destino_id) REFERENCES movimentacao_financeira(id),
 CONSTRAINT ck_transferencia_contas CHECK (conta_origem_id <> conta_destino_id),
 CONSTRAINT ck_transferencia_valor CHECK (valor > 0)
);

CREATE TABLE dizimista (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, pessoa_id BIGINT NOT NULL, organizacao_eclesial_id BIGINT NOT NULL,
 data_inicio DATE NOT NULL, data_fim DATE, ativo BOOLEAN NOT NULL DEFAULT TRUE, observacao TEXT,
 CONSTRAINT fk_dizimista_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id),
 CONSTRAINT fk_dizimista_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT ck_dizimista_periodo CHECK (data_fim IS NULL OR data_fim >= data_inicio)
);
CREATE UNIQUE INDEX uq_dizimista_ativo ON dizimista(pessoa_id,organizacao_eclesial_id) WHERE data_fim IS NULL AND ativo;
CREATE INDEX idx_dizimista_org ON dizimista(organizacao_eclesial_id);

CREATE TABLE contribuicao (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, organizacao_eclesial_id BIGINT NOT NULL,
 pessoa_id BIGINT, dizimista_id BIGINT, categoria_financeira_id BIGINT NOT NULL,
 tipo VARCHAR(20) NOT NULL, valor NUMERIC(15,2) NOT NULL,
 data_contribuicao DATE NOT NULL, competencia DATE, meio_recebimento VARCHAR(20) NOT NULL, observacao TEXT,
 CONSTRAINT fk_contribuicao_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT fk_contribuicao_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id),
 CONSTRAINT fk_contribuicao_dizimista FOREIGN KEY (dizimista_id) REFERENCES dizimista(id),
 CONSTRAINT fk_contribuicao_categoria FOREIGN KEY (categoria_financeira_id) REFERENCES categoria_financeira(id),
 CONSTRAINT ck_contribuicao_tipo CHECK (tipo IN ('DIZIMO','DOACAO','OFERTA','CAMPANHA','TAXA','OUTRA')),
 CONSTRAINT ck_contribuicao_meio CHECK (meio_recebimento IN ('DINHEIRO','PIX','TED','DOC','BOLETO','CARTAO','CHEQUE','OUTRO')),
 CONSTRAINT ck_contribuicao_valor CHECK (valor > 0)
);
CREATE INDEX idx_contribuicao_org_data ON contribuicao(organizacao_eclesial_id,data_contribuicao);
CREATE INDEX idx_contribuicao_pessoa ON contribuicao(pessoa_id);

CREATE TABLE destinacao_contribuicao (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, contribuicao_id BIGINT NOT NULL, centro_custo_id BIGINT,
 tipo VARCHAR(30) NOT NULL, descricao VARCHAR(500), valor NUMERIC(15,2) NOT NULL,
 uso_exclusivo BOOLEAN NOT NULL DEFAULT FALSE,
 CONSTRAINT fk_destinacao_contribuicao FOREIGN KEY (contribuicao_id) REFERENCES contribuicao(id) ON DELETE CASCADE,
 CONSTRAINT fk_destinacao_centro_custo FOREIGN KEY (centro_custo_id) REFERENCES centro_custo(id),
 CONSTRAINT ck_destinacao_tipo CHECK (tipo IN ('LIVRE','FINALIDADE_ESPECIFICA')),
 CONSTRAINT ck_destinacao_valor CHECK (valor > 0),
 CONSTRAINT ck_destinacao_exclusiva CHECK (uso_exclusivo = FALSE OR tipo = 'FINALIDADE_ESPECIFICA')
);

CREATE TABLE contribuicao_movimentacao (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, contribuicao_id BIGINT NOT NULL,
 movimentacao_financeira_id BIGINT NOT NULL, valor NUMERIC(15,2) NOT NULL,
 CONSTRAINT fk_contrib_mov_contribuicao FOREIGN KEY (contribuicao_id) REFERENCES contribuicao(id) ON DELETE CASCADE,
 CONSTRAINT fk_contrib_mov_movimentacao FOREIGN KEY (movimentacao_financeira_id) REFERENCES movimentacao_financeira(id),
 CONSTRAINT ck_contrib_mov_valor CHECK (valor > 0),
 CONSTRAINT uq_contrib_mov UNIQUE(contribuicao_id,movimentacao_financeira_id)
);
CREATE INDEX idx_contrib_mov_contribuicao ON contribuicao_movimentacao(contribuicao_id);
CREATE INDEX idx_contrib_mov_movimentacao ON contribuicao_movimentacao(movimentacao_financeira_id);

CREATE TABLE fornecedor (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, tipo_pessoa VARCHAR(20) NOT NULL, nome VARCHAR(200) NOT NULL,
 cpf_cnpj VARCHAR(14), email VARCHAR(200), telefone VARCHAR(30), ativo BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT ck_fornecedor_tipo_pessoa CHECK (tipo_pessoa IN ('FISICA','JURIDICA'))
);
CREATE UNIQUE INDEX uq_fornecedor_cpf_cnpj ON fornecedor(cpf_cnpj) WHERE cpf_cnpj IS NOT NULL;

CREATE TABLE regra_repasse (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, organizacao_origem_id BIGINT NOT NULL, organizacao_destino_id BIGINT NOT NULL,
 tipo_base VARCHAR(30) NOT NULL, percentual NUMERIC(7,4), valor_fixo NUMERIC(15,2),
 periodicidade VARCHAR(20) NOT NULL, ativo BOOLEAN NOT NULL DEFAULT TRUE,
 data_inicio DATE NOT NULL, data_fim DATE, observacao TEXT,
 CONSTRAINT fk_regra_repasse_origem FOREIGN KEY (organizacao_origem_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT fk_regra_repasse_destino FOREIGN KEY (organizacao_destino_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT ck_regra_repasse_orgs CHECK (organizacao_origem_id <> organizacao_destino_id),
 CONSTRAINT ck_regra_repasse_base CHECK (tipo_base IN ('RECEITA_BRUTA','RECEITA_LIQUIDA','CONTRIBUICOES','DIZIMO','VALOR_FIXO')),
 CONSTRAINT ck_regra_repasse_periodicidade CHECK (periodicidade IN ('MENSAL','BIMESTRAL','TRIMESTRAL','SEMESTRAL','ANUAL','EVENTUAL')),
 CONSTRAINT ck_regra_repasse_percentual CHECK (percentual IS NULL OR percentual >= 0),
 CONSTRAINT ck_regra_repasse_valor_fixo CHECK (valor_fixo IS NULL OR valor_fixo >= 0),
 CONSTRAINT ck_regra_repasse_periodo CHECK (data_fim IS NULL OR data_fim >= data_inicio),
 CONSTRAINT ck_regra_repasse_parametros CHECK ((tipo_base = 'VALOR_FIXO' AND valor_fixo IS NOT NULL) OR (tipo_base <> 'VALOR_FIXO' AND percentual IS NOT NULL))
);

CREATE TABLE obrigacao_repasse (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, regra_repasse_id BIGINT NOT NULL, competencia DATE NOT NULL,
 valor_base NUMERIC(15,2) NOT NULL, valor_exclusoes NUMERIC(15,2) NOT NULL DEFAULT 0,
 valor_repasse NUMERIC(15,2) NOT NULL, data_vencimento DATE NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'ABERTA', observacao TEXT,
 CONSTRAINT fk_obrigacao_repasse_regra FOREIGN KEY (regra_repasse_id) REFERENCES regra_repasse(id),
 CONSTRAINT ck_obrigacao_repasse_status CHECK (status IN ('ABERTA','PAGA','CANCELADA','VENCIDA')),
 CONSTRAINT ck_obrigacao_repasse_valores CHECK (valor_base >= 0 AND valor_exclusoes >= 0 AND valor_repasse >= 0 AND valor_exclusoes <= valor_base)
);
CREATE UNIQUE INDEX uq_obrigacao_repasse_competencia ON obrigacao_repasse(regra_repasse_id,competencia);
CREATE INDEX idx_obrigacao_repasse_regra ON obrigacao_repasse(regra_repasse_id);

CREATE TABLE conta_a_pagar (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, organizacao_eclesial_id BIGINT NOT NULL,
 fornecedor_id BIGINT, obrigacao_repasse_id BIGINT UNIQUE,
 descricao VARCHAR(500) NOT NULL, data_emissao DATE NOT NULL, data_vencimento DATE NOT NULL,
 valor_total NUMERIC(15,2) NOT NULL, status VARCHAR(30) NOT NULL DEFAULT 'ABERTA',
 CONSTRAINT fk_conta_pagar_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT fk_conta_pagar_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id),
 CONSTRAINT fk_conta_pagar_repasse FOREIGN KEY (obrigacao_repasse_id) REFERENCES obrigacao_repasse(id),
 CONSTRAINT ck_conta_pagar_status CHECK (status IN ('ABERTA','PARCIALMENTE_PAGA','PAGA','CANCELADA','VENCIDA')),
 CONSTRAINT ck_conta_pagar_valor CHECK (valor_total > 0),
 CONSTRAINT ck_conta_pagar_vencimento CHECK (data_vencimento >= data_emissao),
 CONSTRAINT ck_conta_pagar_origem CHECK ((fornecedor_id IS NOT NULL AND obrigacao_repasse_id IS NULL) OR (fornecedor_id IS NULL AND obrigacao_repasse_id IS NOT NULL))
);
CREATE INDEX idx_conta_pagar_org ON conta_a_pagar(organizacao_eclesial_id);

CREATE TABLE parcela_a_pagar (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, conta_a_pagar_id BIGINT NOT NULL,
 numero INTEGER NOT NULL, vencimento DATE NOT NULL, valor NUMERIC(15,2) NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'ABERTA',
 CONSTRAINT fk_parcela_conta FOREIGN KEY (conta_a_pagar_id) REFERENCES conta_a_pagar(id) ON DELETE CASCADE,
 CONSTRAINT ck_parcela_numero CHECK (numero > 0),
 CONSTRAINT ck_parcela_valor CHECK (valor > 0),
 CONSTRAINT ck_parcela_status CHECK (status IN ('ABERTA','PAGA','CANCELADA','VENCIDA'))
);
CREATE UNIQUE INDEX uq_parcela_numero ON parcela_a_pagar(conta_a_pagar_id,numero);
CREATE INDEX idx_parcela_conta ON parcela_a_pagar(conta_a_pagar_id);

CREATE TABLE pagamento (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, valor NUMERIC(15,2) NOT NULL,
 data_pagamento DATE NOT NULL, meio_pagamento VARCHAR(20) NOT NULL, observacao TEXT,
 CONSTRAINT ck_pagamento_valor CHECK (valor > 0),
 CONSTRAINT ck_pagamento_meio CHECK (meio_pagamento IN ('DINHEIRO','PIX','TED','DOC','BOLETO','CARTAO','CHEQUE','OUTRO'))
);

CREATE TABLE alocacao_pagamento (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, pagamento_id BIGINT NOT NULL, parcela_a_pagar_id BIGINT NOT NULL,
 valor NUMERIC(15,2) NOT NULL,
 CONSTRAINT fk_alocacao_pagamento FOREIGN KEY (pagamento_id) REFERENCES pagamento(id) ON DELETE CASCADE,
 CONSTRAINT fk_alocacao_parcela FOREIGN KEY (parcela_a_pagar_id) REFERENCES parcela_a_pagar(id),
 CONSTRAINT ck_alocacao_valor CHECK (valor > 0),
 CONSTRAINT uq_alocacao_pagamento_parcela UNIQUE(pagamento_id,parcela_a_pagar_id)
);
CREATE INDEX idx_alocacao_pagamento_pagamento ON alocacao_pagamento(pagamento_id);
CREATE INDEX idx_alocacao_pagamento_parcela ON alocacao_pagamento(parcela_a_pagar_id);

CREATE TABLE pagamento_movimentacao (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 uuid UUID NOT NULL UNIQUE, pagamento_id BIGINT NOT NULL,
 movimentacao_financeira_id BIGINT NOT NULL, valor NUMERIC(15,2) NOT NULL,
 CONSTRAINT fk_pag_mov_pagamento FOREIGN KEY (pagamento_id) REFERENCES pagamento(id) ON DELETE CASCADE,
 CONSTRAINT fk_pag_mov_movimentacao FOREIGN KEY (movimentacao_financeira_id) REFERENCES movimentacao_financeira(id),
 CONSTRAINT ck_pag_mov_valor CHECK (valor > 0),
 CONSTRAINT uq_pag_mov UNIQUE(pagamento_id,movimentacao_financeira_id)
);
CREATE INDEX idx_pagamento_mov_pagamento ON pagamento_movimentacao(pagamento_id);
CREATE INDEX idx_pagamento_mov_movimentacao ON pagamento_movimentacao(movimentacao_financeira_id);

COMMENT ON SCHEMA adm_eclesial IS 'Modelo administrativo eclesial - DER Consolidado v2.0';
COMMENT ON TABLE familia IS 'Agrupamento opcional de pessoas; não é criado automaticamente no cadastro da pessoa.';
COMMENT ON TABLE obrigacao_repasse IS 'Snapshot histórico da obrigação de repasse por competência.';

