create table public.clientes (
    id                  serial primary key,
    token               uuid not null default gen_random_uuid() unique,
    nome                text not null,
    apelido             text, 
    cpf_cnpj            text unique, 
    email               text,
    telefone            text,
    cep                 text,
    uf                  char(2),
    ativo               boolean not null default true,
    criado_em           timestamp with time zone not null default now(),
    atualizado_em       timestamp with time zone not null default now()
);


create index idx_clientes_cpf_cnpj on public.clientes (cpf_cnpj);


create trigger trigger_set_updated_at_clientes
before update on public.clientes
for each row
execute function public.set_updated_at();