
CREATE TYPE status_pedido_compra AS ENUM (
    'RASCUNHO',
    'ENVIADO',
    'CONFIRMADO',
    'FINALIZADO',
    'CANCELADO'
);

create table public.pedido_compra (
    id serial primary key,
    token uuid not null default gen_random_uuid() unique,
    fornecedor_id integer not null references public.fornecedores(id) on delete cascade,
    valor_total numeric(10, 2) not null,
    status status_pedido_compra not null default 'RASCUNHO',
    observacao text,
    data_emissao date not null default current_date,
    data_entrega date,
    criado_em timestamp with time zone not null default now(),
    atualizado_em timestamp with time zone not null default now()
);

CREATE INDEX idx_pedido_compra_fornecedor_id 
ON public.pedido_compra (fornecedor_id);

-- Atualiza automaticamente atualizado_em
create or replace function public.set_updated_at()
returns trigger as $$
begin
  new.atualizado_em = now();
  return new;
end;
$$ language plpgsql;

create trigger trigger_set_updated_at
before update on public.pedido_compra
for each row
execute function public.set_updated_at();