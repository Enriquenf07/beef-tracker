'use server'
import { createApi } from "@/app/lib/api";
import { revalidatePath } from "next/cache";

export async function handleCadastro(body: Record<string, any>) {
    try {
        const id = body.id
        const api = await createApi()
        if (!id) {
            await api.post("/usuario", body)
        } else {
            await api.put("/usuario/" + id, body)
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
        revalidatePath('/ui/usuarios')
    }
}

export async function handleReenviarEmail(id: number) {
    const api = await createApi()
    try {
        await api.post("/usuario/" + id + '/reenviar-email')
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/usuarios')
    }
}

export async function handleExcluir(id: number) {
    const api = await createApi()
    try {
        await api.delete("/usuario/" + id)
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/usuarios')
    }
}