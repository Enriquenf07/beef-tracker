'use server';

import { createApi } from "@/app/lib/api";
import { revalidatePath } from 'next/cache';


export async function getPedidos(chave?: string, status?: string) {
    const api = await createApi();
    try {

        const { data } = await api.get('/compras/pedido', {
            params: {
                chave: chave || "",
                status: (status && status !== 'null') ? status : ""
            }
        }) as any;

        return data;
    } catch (e) {
        console.error("Erro ao buscar pedidos:", e);
        return [];
    }
}



export async function handleCadastroPedido(formData: any) {
    const api = await createApi();
    try {
        await api.post("/compras/pedido", formData);


        revalidatePath("/ui/compras/pedidos");

        return { success: true };
    } catch (e: any) {
        console.error("Erro no cadastro:", e);
        return {
            error: e.response?.data || "Erro ao salvar pedido no servidor"
        };
    }
}