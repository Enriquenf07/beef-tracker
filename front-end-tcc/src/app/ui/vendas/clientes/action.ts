'use server'
import { createApi } from "@/app/lib/api";
import { revalidatePath } from "next/cache";

export async function handleCadastro(formData: FormData) {
    'use server'
    try {
        const nome = formData.get('nome')
        const apelido = formData.get('apelido')
        const cpfCnpj = formData.get('cpfCnpj')
        const email = formData.get('email')
        const telefone = formData.get('telefone')
        const cep = formData.get('cep')
        const uf = formData.get('uf')
        const id = formData.get('id')
        const api = await createApi()
        if (!id) {
            await api.post("/vendas/cliente", {
                nome, apelido, cpfCnpj, email, telefone, cep, uf
            })
        } else {
            await api.put("/vendas/cliente/" + id, {
                nome, apelido, cpfCnpj, email, telefone, cep, uf
            })
        }
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/vendas/clientes')
    }
}

export async function handleInativar(id: number) {
    const api = await createApi()
    try {
        await api.patch("/vendas/cliente/" + id + '/status')
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/vendas/clientes')
    }
}