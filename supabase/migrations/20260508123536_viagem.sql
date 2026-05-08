
create table public.viagem (
    id                  serial primary key,
    token               uuid not null default gen_random_uuid() unique,
    veiculo_id         integer references public.veiculo(id),
    sensor_id           integer references public.sensor(id),
    status_viagem          text,
    saida_em          timestamp with time zone,
    entregue_em          timestamp with time zone,
    criado_em           timestamp with time zone not null default now(),
    atualizado_em       timestamp with time zone not null default now()
);

create index idx_viagem_veiculo_id      on public.viagem (veiculo_id);
create index idx_viagem_sensor_id        on public.viagem (sensor_id);

create trigger trigger_set_updated_at
before update on public.viagem
for each row
execute function public.set_updated_at();


create table public.viagem_compra (
    id                  serial primary key,
    token               uuid not null default gen_random_uuid() unique,
    viagem_id           integer not null references public.viagem(id),
    pedido_compra_id    integer not null references public.pedido_compra(id)
);

create index idx_viagem_compra_viagem_id on public.viagem_compra (viagem_id);
create index idx_viagem_compra_pedido_id on public.viagem_compra (pedido_compra_id);

create trigger trigger_set_updated_at
before update on public.viagem_compra
for each row
execute function public.set_updated_at();