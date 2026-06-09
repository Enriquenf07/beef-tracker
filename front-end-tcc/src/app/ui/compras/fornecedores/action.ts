'use server'
import { createApi } from "@/app/lib/api";
import { revalidatePath } from "next/cache";

export async function handleCadastro(formData: FormData) {
    try {
        const nome = formData.get('nome')
        const apelido = formData.get('apelido')
        const cnpj = formData.get('cnpj')
        const cep = formData.get('cep')
        const endereco = formData.get('endereco')
        const id = formData.get('id')
        const api = await createApi()
        if (!id) {
            await api.post("/compras/fornecedor", { nome, apelido, cnpj, cep, endereco })
        } else {
            await api.put("/compras/fornecedor/" + id, { nome, apelido, cnpj, cep, endereco })
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
        await api.patch("/compras/fornecedor/" + id + '/status')
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/compras/fornecedores')
    }
}

export async function handleExcluir(id: number) {
    const api = await createApi()
    try {
        await api.delete("/compras/fornecedor/" + id)
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/compras/fornecedores')
    }
}