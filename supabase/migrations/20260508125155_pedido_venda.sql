create table public.pedido_venda (
    id serial primary key,
    token uuid not null default gen_random_uuid() unique,
    cliente_id integer not null references public.clientes(id) on delete cascade,
    valor_total numeric(10, 2) not null default 0.00,
    status text not null default 'RASCUNHO', 
    observacao text,
    data_venda timestamp with time zone not null default now(),
    data_vencimento date,
    criado_em timestamp with time zone not null default now(),
    atualizado_em timestamp with time zone not null default now()
);

CREATE INDEX idx_pedido_venda_cliente_id 
ON public.pedido_venda (cliente_id);

create trigger trigger_set_updated_at_venda
before update on public.pedido_venda
for each row
execute function public.set_updated_at();