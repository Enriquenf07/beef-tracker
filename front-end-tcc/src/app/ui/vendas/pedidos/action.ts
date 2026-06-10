'use server'

import { createApi } from "@/app/lib/api"
import { revalidatePath } from "next/cache"

export async function handleCadastro(formData: FormData) {
    const api = await createApi()
    const id = formData.get('id')

    const body = {
        clienteId: Number(formData.get('clienteId')),
        valorTotal: Number(formData.get('valorTotal')),
        observacao: formData.get('observacao'),
        dataVenda: formData.get('dataVenda') || null,
        dataVencimento: formData.get('dataVencimento') || null,
    }

    try {
        if (id) {
            await api.put(`/vendas/pedido/${id}`, body)
        } else {
            await api.post('/vendas/pedido', body)
        }
        revalidatePath('/ui/vendas/pedidos')
        return null
    } catch (e: any) {
        return e?.response?.data
    }
}

export async function handleLoteCadastro(pedidoId: number, formData: FormData) {
    const api = await createApi()
    const id = formData.get('loteId')

    const body = {
        nome: formData.get('nome'),
        descricao: formData.get('descricao'),
        peso: Number(formData.get('peso')),
        pedidoVendaId: pedidoId,
        loteOriginalId: Number(formData.get('loteOriginalId')),
    }

    try {
        if (id) {
            await api.put(`/vendas/pedido/${pedidoId}/lote/${id}`, body)
        } else {
            await api.post(`/vendas/pedido/${pedidoId}/lote`, body)
        }
        revalidatePath('/ui/vendas/pedidos')
        return null
    } catch (e: any) {
        return e?.response?.data
    }
}

export async function handleVincularViagem(formData: FormData, id: any) {
    const api = await createApi()
    const viagemId = formData.get('viagemId')

    try {
        if (id) {
            await api.patch(`/vendas/pedido/${id}/vincular-viagem/${viagemId}`)
        }

        revalidatePath('/ui/vendas/pedidos')
        return null
    } catch (e: any) {
        return e?.response?.data
    }
}

export async function handleAtualizarStatus(id: number, status: string) {
    const api = await createApi()
    try {
        await api.patch(`/vendas/pedido/${id}/status`, { status })
        revalidatePath('/ui/vendas/pedidos')
        return null
    } catch (e: any) {
        return e?.response?.data
    }
}