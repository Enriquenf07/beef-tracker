'use server'

import { createApi } from "@/app/lib/api";
import { revalidatePath } from "next/cache";

export async function handleCadastroSensor(formData: FormData) {
    try {
        const id = formData.get('id');
        const api = await createApi();

        const payload = {
            descricao: formData.get('descricao'),
            ativo: true,
        };

        if (!id) {
            await api.post("/api/sensor", payload);
        } else {
            await api.put(`/api/sensor/${id}`, payload);
        }
    } catch (e: any) {
        return e.response?.data;
    } finally {
        revalidatePath('/ui/sensores');
    }
}

export async function handleAlterarStatusSensor(id: number) {
    try {
        const api = await createApi();
        await api.patch(`/api/sensor/${id}/status`);
    } catch (e: any) {
        return e.response?.data;
    } finally {
        revalidatePath('/ui/sensores');
    }
}
export async function handleExcluirSensor(id: number) {
    try {
        const api = await createApi();
        await api.delete(`/api/sensor/${id}`);
    } catch (e: any) {
        return e.response?.data;
    } finally {
        revalidatePath('/ui/sensores');
    }
}