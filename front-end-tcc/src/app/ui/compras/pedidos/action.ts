'use server'

import { createApi } from "@/app/lib/api"
import { revalidatePath } from "next/cache"

export async function handleCadastro(
    formData: FormData
) {
    const api = await createApi()

    const id = formData.get('id')

    const body = {
        fornecedorId: Number(
            formData.get('fornecedorId')
        ),

        valorTotal: Number(
            formData.get('valorTotal')
        ),

        observacao: formData.get('observacao'),

        dataEmissao: formData.get('dataEmissao'),

        dataEntrega: formData.get('dataEntrega'),
    }

    try {
        if (id) {
            await api.put(
                `/compras/pedido/${id}`,
                body
            )
        } else {
            await api.post(
                '/compras/pedido',
                body
            )
        }

        revalidatePath('/ui/compras/pedidos')

        return null
    } catch (e: any) {
        return e?.response?.data
    }
}

export async function handleAtualizarStatus(
    id: number,
    status: string
) {
    const api = await createApi()

    try {
        await api.patch(
            `/compras/pedido/${id}/status`,
            { status }
        )

        revalidatePath('/ui/compras/pedidos')

        return null
    } catch (e: any) {
        return e?.response?.data
    }
}