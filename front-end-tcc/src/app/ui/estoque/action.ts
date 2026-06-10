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
        const loteOriginalId = formData.get('loteOriginalId')
        const body = {
            nome: formData.get('nome'),
            descricao: formData.get('descricao') || null,
            peso: formData.get('peso') ? Number(formData.get('peso')) : null,
            pedidoVendaId: Number(formData.get('pedidoVendaId')),
        }
        await api.post(`/compras/lote/${loteOriginalId}/fracao`, body)
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/estoque')
    }
}

export async function handleDeleteLoteBruto(id: number) {
    const api = await createApi()
    try {
        await api.delete(`/compras/lote/${id}`)
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/estoque')
    }
}

export async function handleDeleteLoteFracionado(id: number) {
    const api = await createApi()
    try {
        await api.delete(`/compras/lote/fracao/${id}`)
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/estoque')
    }
}