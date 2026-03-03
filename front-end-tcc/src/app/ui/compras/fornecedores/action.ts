'use server'
import { createApi } from "@/app/lib/api";
import { revalidatePath } from "next/cache";
import { redirect, RedirectType } from "next/navigation";

export async function handleCadastro(formData: FormData) {
    'use server'
    try {
        const nome = formData.get('nome')
        const apelido = formData.get('apelido')
        const cnpj = formData.get('cnpj')
        const cep = formData.get('cep')
        const endereco = formData.get('endereco')
        const id = formData.get('id')
        const api = await createApi()
        if (!id) {
            const { data } = await api.post("/compras/fornecedor", {
                nome, apelido, cnpj, cep, endereco
            }) as any
        } else {
            const { data } = await api.put("/compras/fornecedor/" + id, {
                nome, apelido, cnpj, cep, endereco
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
        await api.put("/compras/fornecedor/" + id + '/status', {
            status
        })
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/compras/fornecedores')
    }

}