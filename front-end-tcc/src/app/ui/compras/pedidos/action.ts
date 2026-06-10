'use server'

import { createApi } from "@/app/lib/api"
import { revalidatePath } from "next/cache"

export async function handleCadastro(
    formData: FormData
) {
    console.log('1')

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

export async function handleLoteCadastro(
    formData: FormData, id: any
) {
    const api = await createApi()
    console.log('2')


    const body = {
        observacao: formData.get('observacao'),
        nome: formData.get('nome'),
        peso: formData.get('peso'),
    }

    try {
        if (id) {
            await api.post(
                `/compras/pedido/${id}/lote`,
                body
            )
        }

        revalidatePath('/ui/compras/pedidos')

        return null
    } catch (e: any) {
        return e?.response?.data
    }
}

export async function handleVincularViagem(
    formData: FormData, id: any
) {
    console.log('3')
    const api = await createApi()
    const viagemId = formData.get('viagemId')

    try {
        if (id) {
            await api.patch(
                `/compras/pedido/${id}/vincular-viagem/${viagemId}`
            )
        }
        console.log(`/compras/pedido/${id}/vincular-viagem/${viagemId}`)

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