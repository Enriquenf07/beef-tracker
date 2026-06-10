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
<<<<<<< Updated upstream
        const loteOriginalId = formData.get('loteOriginalId')
=======
        const pedidoVendaId = formData.get('pedidoVendaId')
>>>>>>> Stashed changes
        const body = {
            nome: formData.get('nome'),
            descricao: formData.get('descricao') || null,
            peso: formData.get('peso') ? Number(formData.get('peso')) : null,
<<<<<<< Updated upstream
            pedidoVendaId: Number(formData.get('pedidoVendaId')),
        }
        await api.post(`/compras/lote/${loteOriginalId}/fracao`, body)
=======
            loteOriginalId: Number(formData.get('loteOriginalId')),
            pedidoVendaId: Number(pedidoVendaId),
        }
        await api.post(`/vendas/pedido/${pedidoVendaId}/lote`, body)
>>>>>>> Stashed changes
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/estoque')
    }
}

<<<<<<< Updated upstream
export async function handleDeleteLoteBruto(id: number) {
    const api = await createApi()
    try {
        await api.delete(`/compras/lote/${id}`)
=======
export async function handleDeleteLoteBruto(pedidoId: number, loteId: number) {
    const api = await createApi()
    try {
        await api.delete(`/compras/pedido/${pedidoId}/lote/${loteId}`)
>>>>>>> Stashed changes
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/estoque')
    }
}

<<<<<<< Updated upstream
export async function handleDeleteLoteFracionado(id: number) {
    const api = await createApi()
    try {
        await api.delete(`/compras/lote/fracao/${id}`)
=======
export async function handleDeleteLoteFracionado(pedidoVendaId: number, loteId: number) {
    const api = await createApi()
    try {
        await api.delete(`/vendas/pedido/${pedidoVendaId}/lote/${loteId}`)
>>>>>>> Stashed changes
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/estoque')
    }
}