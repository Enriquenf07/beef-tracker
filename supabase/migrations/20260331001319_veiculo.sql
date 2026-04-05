-- Tabela
create table public.veiculo (
    id                serial primary key,
    token             uuid not null default gen_random_uuid() unique,
    placa             text not null unique,
    modelo            text not null,
    marca             text not null,
    ano               smallint not null,
    capacidade_carga  numeric(15, 4) not null,
    ativo             boolean not null default true,
    criado_em         timestamp with time zone not null default now(),
    atualizado_em     timestamp with time zone not null default now()
);

-- Trigger (função já existe no banco)
create trigger trigger_set_updated_at
before update on public.veiculo
for each row
execute function public.set_updated_at();