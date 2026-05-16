'use server'

import { createApi } from "@/app/lib/api"
import { revalidatePath } from "next/cache"

export async function handleCadastro(
    formData: FormData
) {
    const api = await createApi()

    const id = formData.get('id')

    const body = {
        clienteId: Number(
            formData.get('clienteId')
        ),

        valorTotal: Number(
            formData.get('valorTotal')
        ),

        observacao: formData.get('observacao'),

        dataVenda: formData.get('dataVenda') || null,

        dataVencimento: formData.get('dataVencimento') || null,
    }

    try {
        if (id) {
            await api.put(
                `/vendas/pedido/${id}`,
                body
            )
        } else {
            await api.post(
                '/vendas/pedido',
                body
            )
        }

        revalidatePath('/ui/vendas/pedidos')

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
            `/vendas/pedido/${id}/status`,
            { status }
        )

        revalidatePath('/ui/vendas/pedidos')

        return null
    } catch (e: any) {
        return e?.response?.data
    }
}