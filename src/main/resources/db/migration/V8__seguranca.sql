SET search_path TO adm_eclesial, public;
CREATE TABLE usuario_perfil (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 usuario_id BIGINT NOT NULL,
 perfil VARCHAR(30) NOT NULL,
 CONSTRAINT fk_usuario_perfil_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
 CONSTRAINT ck_usuario_perfil CHECK (perfil IN ('ADMINISTRADOR','SECRETARIA','CATEQUESE','FINANCEIRO','CONSULTA')),
 CONSTRAINT uq_usuario_perfil UNIQUE (usuario_id, perfil)
);
CREATE INDEX idx_usuario_perfil_usuario ON usuario_perfil(usuario_id);
CREATE TABLE usuario_organizacao (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 usuario_id BIGINT NOT NULL,
 organizacao_eclesial_id BIGINT NOT NULL,
 CONSTRAINT fk_usuario_organizacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
 CONSTRAINT fk_usuario_organizacao_org FOREIGN KEY (organizacao_eclesial_id) REFERENCES organizacao_eclesial(id),
 CONSTRAINT uq_usuario_organizacao UNIQUE (usuario_id, organizacao_eclesial_id)
);
CREATE INDEX idx_usuario_organizacao_usuario ON usuario_organizacao(usuario_id);
CREATE INDEX idx_usuario_organizacao_org ON usuario_organizacao(organizacao_eclesial_id);
