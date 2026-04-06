"use server";

import { createApi } from "@/app/lib/api";
import { revalidatePath } from "next/cache";

export async function handleCadastro(formData: FormData) {
    try {
        const placa = formData.get("placa");
        const modelo = formData.get("modelo");
        const marca = formData.get("marca");
        const ano = formData.get("ano");
        const capacidadeCarga = formData.get("capacidadeCarga");
        const ativo = formData.get("ativo") === "on";

        const api = await createApi();

        await api.post("/api/veiculos", {
            placa,
            modelo,
            marca,
            ano: ano ? Number(ano) : null,
            capacidadeCarga: Number(capacidadeCarga),
            ativo,
        });

    } catch (e: any) {
        return e.response?.data;
    } finally {
        revalidatePath("/ui/veiculos");
    }
}