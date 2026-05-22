'use server'
import { createApi } from "@/app/lib/api";
import { revalidatePath } from "next/cache";
import { redirect, RedirectType } from "next/navigation";
export async function handleCadastro(formData: FormData) {
    'use server'
    try {
        const id = formData.get('id')
        const descricao = formData.get('descricao')
        const api = await createApi()

        if (!id) {
            await api.post("/viagem", {
                veiculoId: formData.get('veiculoId'),
                sensorId: formData.get('sensorId'),
                sensorToken: formData.get('sensorToken'),
                descricao,
                saidaEm: formData.get('saidaEm')
            })
        } else {
            await api.put("/viagem/" + id, { descricao })
        }
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/viagens')
    }
}

export async function handleEmTransito(id: number) {
    'use server'
    try {
        const api = await createApi()
        await api.patch("/viagem/" + id + "/status", { novoStatus: "EM_TRANSITO" })
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/viagens')
    }
}

export async function handleEntregue(id: number) {
    'use server'
    try {
        const api = await createApi()
        await api.patch("/viagem/" + id + "/status", { novoStatus: "ENTREGUE" })
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/viagens')
    }
}

export async function handleCancelar(id: number) {
    'use server'
    try {
        const api = await createApi()
        await api.patch("/viagem/" + id + "/status", { novoStatus: "CANCELADA" })
    } catch (e: any) {
        return e.response?.data
    } finally {
        revalidatePath('/ui/viagens')
    }
}