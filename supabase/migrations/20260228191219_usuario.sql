create extension if not exists "pgcrypto";

create table public.usuarios (
    id serial primary key,
    token uuid not null default gen_random_uuid() unique,
    nome text not null,
    email text not null unique,
    senha text not null,
    ativo boolean not null default true,
    criado_em timestamp with time zone not null default now(),
    atualizado_em timestamp with time zone not null default now()
);

-- Atualiza automaticamente atualizado_em
create or replace function public.set_updated_at()
returns trigger as $$
begin
  new.atualizado_em = now();
  return new;
end;
$$ language plpgsql;

create trigger trigger_set_updated_at
before update on public.usuarios
for each row
execute function public.set_updated_at();