SET search_path TO adm_eclesial, public;

-- Funções eclesiais padrão (insere apenas as que ainda não existem, por código).
-- Necessário, por exemplo, para a função CATEQUISTA usada na elegibilidade de catequistas.
INSERT INTO funcao_eclesial (uuid, codigo, nome, descricao, ativo, exclusivo_na_organizacao)
SELECT v.uuid, v.codigo, v.nome, v.descricao, TRUE, FALSE
FROM (VALUES
    ('d0000000-0000-0000-0000-000000000001'::uuid, 'PAROCO',     'Pároco',        'Pároco responsável pela paróquia'),
    ('d0000000-0000-0000-0000-000000000002'::uuid, 'VIGARIO',    'Vigário',       'Vigário paroquial'),
    ('d0000000-0000-0000-0000-000000000003'::uuid, 'DIACONO',    'Diácono',       'Diácono permanente'),
    ('d0000000-0000-0000-0000-000000000004'::uuid, 'CATEQUISTA', 'Catequista',    'Catequista de etapa/turma'),
    ('d0000000-0000-0000-0000-000000000005'::uuid, 'SECRETARIO', 'Secretário(a)', 'Secretaria paroquial'),
    ('d0000000-0000-0000-0000-000000000006'::uuid, 'TESOUREIRO', 'Tesoureiro(a)', 'Tesouraria/administração financeira')
) AS v(uuid, codigo, nome, descricao)
WHERE NOT EXISTS (
    SELECT 1 FROM funcao_eclesial f WHERE f.codigo = v.codigo
);
