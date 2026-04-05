-- Tabela
create table public.viagem_compra (
    id                  serial primary key,
    token               uuid not null default gen_random_uuid() unique,
    veiculo_id         integer not null references public.veiculo(id),
    sensor_id           integer references public.sensor(id),
    pedido_compra_id    integer not null references public.pedido_compra(id),
    entregue_em          timestamp with time zone,
    criado_em           timestamp with time zone not null default now(),
    atualizado_em       timestamp with time zone not null default now()
);

create index idx_viagem_compra_veiculo_id      on public.viagem_compra (veiculo_id);
create index idx_viagem_compra_sensor_id        on public.viagem_compra (sensor_id);
create index idx_viagem_compra_pedido_compra_id on public.viagem_compra (pedido_compra_id);

create trigger trigger_set_updated_at
before update on public.viagem_compra
for each row
execute function public.set_updated_at();