ALTER TABLE public.viagem
  ADD COLUMN motorista_id INTEGER NULL,
  ADD CONSTRAINT viagem_motorista_id_fkey
    FOREIGN KEY (motorista_id) REFERENCES usuarios (id);

CREATE INDEX IF NOT EXISTS idx_viagem_motorista_id
  ON public.viagem USING btree (motorista_id) TABLESPACE pg_default;