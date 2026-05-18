-- 20260516000001_lote_fracionado.sql

create table public.lote_fracionado (
    id              serial primary key,
    token           uuid not null default gen_random_uuid() unique,
    nome            text not null,
    descricao       text,
    peso            integer,
    lote_original_id integer not null references public.lote_bruto(id),
    pedido_venda_id integer not null references public.pedido_venda(id),
    criado_em       timestamp with time zone not null default now(),
    atualizado_em   timestamp with time zone not null default now()
);

create trigger trigger_set_updated_at_lote_fracionado
before update on public.lote_fracionado
for each row
execute function public.set_updated_at();