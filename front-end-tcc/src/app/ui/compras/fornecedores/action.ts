'use server'
import { createApi } from "@/app/lib/api";
import { revalidatePath } from "next/cache";
import { redirect, RedirectType } from "next/navigation";

export async function handleCadastro(formData: FormData) {
    'use server'

    let isSuccess = false;

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
            })
        } else {
            const { data } = await api.put("/compras/fornecedor/" + id, {
                nome, apelido, cnpj, cep, endereco
            })
        }
        isSuccess = true;

    } catch (error) {
        console.error('Login failed:', error)
    }

    if (isSuccess) {
        redirect('/ui/compras/fornecedores?success=true', RedirectType.push)
    } else {
        redirect('/ui/compras/fornecedores?success=false', RedirectType.push)
    }
}

export async function handleInativar(id: number, status: boolean) {
    console.log('try', id, status)
    const api = await createApi()
    let isSuccess = false;
    try {
        await api.put("/compras/fornecedor/" + id + '/status', {
            status
        })
        isSuccess = true
    }catch(e){
        isSuccess = false
    }
    
    if (isSuccess) {
        revalidatePath('/ui/compras/fornecedores')
        redirect('/ui/compras/fornecedores?success=true', RedirectType.push)
    } else {
        revalidatePath('/ui/compras/fornecedores')
        redirect('/ui/compras/fornecedores?success=false', RedirectType.push)
    }
}