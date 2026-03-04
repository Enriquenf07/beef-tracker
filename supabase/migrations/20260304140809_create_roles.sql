create table public.roles (
    id serial primary key,
    nome text not null
);

create table public.role_usuario (
    id serial primary key,
    usuario_id integer not null references public.usuarios(id) on delete cascade,
    role_id integer not null references public.roles(id) on delete cascade
);