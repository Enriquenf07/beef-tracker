
ALTER TABLE public.pedido_compra
ALTER COLUMN status DROP DEFAULT;

ALTER TABLE public.pedido_compra
ALTER COLUMN status TYPE text
USING status::text;

ALTER TABLE public.pedido_compra
ALTER COLUMN status SET DEFAULT 'RASCUNHO';

DROP TYPE status_pedido_compra;