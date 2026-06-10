'use server'
import { createApi } from "@/app/lib/api";
import { revalidatePath } from "next/cache";

export async function handleCadastroLoteBruto(formData: FormData) {
    const api = await createApi()
    try {
        const pedidoId = formData.get('pedidoCompraId')
        const body = {
            nome: formData.get('nome'),
            descricao: formData.get('descricao') || null,
            peso: formData.get('peso') ? Number(formData.get('peso')) : null,
        }
        await api.post(`/compras/pedido/${pedidoId}/lote`, body)
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/estoque')
    }
}

export async function handleCadastroLoteFracionado(formData: FormData) {
    const api = await createApi()
    try {
        const pedidoVendaId = formData.get('pedidoVendaId')
        const body = {
            nome: formData.get('nome'),
            descricao: formData.get('descricao') || null,
            peso: formData.get('peso') ? Number(formData.get('peso')) : null,
            loteOriginalId: Number(formData.get('loteOriginalId')),
            pedidoVendaId: Number(pedidoVendaId),
        }
        await api.post(`/vendas/pedido/${pedidoVendaId}/lote`, body)
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/estoque')
    }
}

export async function handleDeleteLoteBruto(pedidoId: number, loteId: number) {
    const api = await createApi()
    try {
        await api.delete(`/compras/pedido/${pedidoId}/lote/${loteId}`)
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/estoque')
    }
}

export async function handleDeleteLoteFracionado(pedidoVendaId: number, loteId: number) {
    const api = await createApi()
    try {
        await api.delete(`/vendas/pedido/${pedidoVendaId}/lote/${loteId}`)
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/estoque')
    }
}