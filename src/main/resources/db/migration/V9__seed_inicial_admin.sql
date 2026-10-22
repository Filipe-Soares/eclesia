SET search_path TO adm_eclesial, public;

-- 1. Inserir Organização Eclesial padrão se não existir nenhuma
INSERT INTO organizacao_eclesial (uuid, nome, nome_reduzido, tipo, ativo, data_cadastro)
SELECT 
    'a0000000-0000-0000-0000-000000000001'::uuid,
    'Paróquia Matriz - Sede',
    'Matriz',
    'PAROQUIA',
    TRUE,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM organizacao_eclesial
);

-- 2. Inserir Pessoa Administradora se não existir
INSERT INTO pessoa (uuid, nome, ativo, data_cadastro)
SELECT 
    'b0000000-0000-0000-0000-000000000001'::uuid,
    'Administrador do Sistema',
    TRUE,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM pessoa WHERE nome = 'Administrador do Sistema'
);

-- 3. Inserir Usuário admin (senha: admin123) se não existir
-- Hash BCrypt para 'admin123': $2a$10$SCyJYPtefIvXnnGMi8uHqepjugM/iD8k8bpXJ64dcuMo3oKDm040q
INSERT INTO usuario (uuid, pessoa_id, login, senha_hash, ativo)
SELECT 
    'c0000000-0000-0000-0000-000000000001'::uuid,
    p.id,
    'admin',
    '$2a$10$SCyJYPtefIvXnnGMi8uHqepjugM/iD8k8bpXJ64dcuMo3oKDm040q',
    TRUE
FROM pessoa p
WHERE p.nome = 'Administrador do Sistema'
  AND NOT EXISTS (
    SELECT 1 FROM usuario WHERE LOWER(login) = 'admin'
  );

-- 4. Inserir Perfil ADMINISTRADOR para o usuário admin
INSERT INTO usuario_perfil (usuario_id, perfil)
SELECT u.id, 'ADMINISTRADOR'
FROM usuario u
WHERE LOWER(u.login) = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_perfil up WHERE up.usuario_id = u.id AND up.perfil = 'ADMINISTRADOR'
  );

-- 5. Inserir Vínculo do usuário admin com a primeira organização ativa
INSERT INTO usuario_organizacao (usuario_id, organizacao_eclesial_id)
SELECT u.id, o.id
FROM usuario u
CROSS JOIN (
    SELECT id FROM organizacao_eclesial WHERE ativo = TRUE ORDER BY id LIMIT 1
) o
WHERE LOWER(u.login) = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_organizacao uo WHERE uo.usuario_id = u.id AND uo.organizacao_eclesial_id = o.id
  );
