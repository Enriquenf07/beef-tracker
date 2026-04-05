ALTER TABLE public.lote_bruto
    ADD COLUMN pedido_compra_id integer not null references public.pedido_compra(id);