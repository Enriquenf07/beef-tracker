-- Tabela
create table public.sensor (
    id            serial primary key,
    token         uuid not null default gen_random_uuid() unique,
    descricao     text,
    ativo         boolean not null default true,
    criado_em     timestamp with time zone not null default now(),
    atualizado_em timestamp with time zone not null default now()
);


create trigger trigger_set_updated_at
before update on public.sensor
for each row
execute function public.set_updated_at();