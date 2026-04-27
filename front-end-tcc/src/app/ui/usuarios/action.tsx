'use server'
import { createApi } from "@/app/lib/api";
import { revalidatePath } from "next/cache";
import { redirect, RedirectType } from "next/navigation";

export async function handleCadastro(body: Record<string, any>) {
    try {
        const id = body.id
        const api = await createApi()
        if (!id) {
            const { data } = await api.post("/usuario", body) as any
        } else {
            const { data } = await api.put("/usuario/" + id, body) as any
        }
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/usuarios')
    }
}


export async function handleInativar(id: number, status: boolean) {
    const api = await createApi()
    try {
        await api.patch("/usuario/" + id + '/status')
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/compras/fornecedores')
    }
}

export async function handleReenviarEmail(id: number) {
    const api = await createApi()
    try {
        await api.post("/usuario/" + id + '/reenviar-email')
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/compras/fornecedores')
    }
}