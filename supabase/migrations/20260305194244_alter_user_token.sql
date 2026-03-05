ALTER TABLE usuarios
ADD COLUMN token_primeiro_acesso text NULL,
ADD COLUMN token_criado_em TIMESTAMPTZ NULL;