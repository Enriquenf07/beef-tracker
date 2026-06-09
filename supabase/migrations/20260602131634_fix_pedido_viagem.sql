ALTER TABLE public.pedido_compra
  ADD COLUMN IF NOT EXISTS viagem_id integer NULL;

ALTER TABLE public.pedido_compra
  ADD CONSTRAINT pedido_compra_viagem_id_fkey
  FOREIGN KEY (viagem_id)
  REFERENCES public.viagem (id)
  ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_pedido_compra_viagem_id
  ON public.pedido_compra USING btree (viagem_id);
ALTER TABLE public.pedido_venda
  ADD COLUMN IF NOT EXISTS viagem_id integer NULL;

ALTER TABLE public.pedido_venda
  ADD CONSTRAINT pedido_venda_viagem_id_fkey
  FOREIGN KEY (viagem_id)
  REFERENCES public.viagem (id)
  ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_pedido_venda_viagem_id
  ON public.pedido_venda USING btree (viagem_id);

DROP TABLE IF EXISTS public.viagem_compra;