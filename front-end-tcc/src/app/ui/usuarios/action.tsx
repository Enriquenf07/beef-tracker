'use server'
import { createApi } from "@/app/lib/api";
import { revalidatePath } from "next/cache";
import { redirect, RedirectType } from "next/navigation";

export async function handleCadastro(formData: FormData) {
    try {
        const nome = formData.get('nome')
        const id = formData.get('id')
        const api = await createApi()
        if (!id) {
            const { data } = await api.post("/usuario", {
                nome
            }) as any
        } else {
            const { data } = await api.put("/compras/" + id, {
                nome
            }) as any
        }
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/compras/fornecedores')
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