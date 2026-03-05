create table public.item_pedido_compra (
    id serial primary key,
    token uuid not null default gen_random_uuid() unique,
    pedido_id integer not null references public.pedido_compra(id) on delete cascade,
    criado_em timestamp with time zone not null default now(),
    atualizado_em timestamp with time zone not null default now()
);

CREATE INDEX idx_item_pedido_compra_pedido_id 
ON public.item_pedido_compra (pedido_id);

-- Atualiza automaticamente atualizado_em
create or replace function public.set_updated_at()
returns trigger as $$
begin
  new.atualizado_em = now();
  return new;
end;
$$ language plpgsql;

create trigger trigger_set_updated_at
before update on public.item_pedido_compra
for each row
execute function public.set_updated_at();