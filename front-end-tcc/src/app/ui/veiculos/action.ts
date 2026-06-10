'use server'

import { createApi } from "@/app/lib/api";
import { revalidatePath } from "next/cache";

export async function handleCadastroVeiculo(formData: FormData) {
  try {
    const id = formData.get('id');
    const api = await createApi();


    const payload = {
      placa: formData.get('placa'),
      modelo: formData.get('modelo'),
      marca: formData.get('marca'),
      ano: parseInt(formData.get('ano') as string),
      capacidadeCarga: parseFloat(formData.get('capacidadeCarga') as string),
      ativo: true
    };

    if (!id) {
      await api.post("/api/veiculos", payload);
    } else {

      await api.put(`/api/veiculos/${id}`, payload);
    }
  } catch (e: any) {
    console.error("ERRO AO SALVAR VEICULO:", e);
    return e.response?.data;
  } finally {
    revalidatePath('/ui/veiculos');
  }
}

export async function handleAlterarStatusVeiculo(id: number) {
  try {
    const api = await createApi();
    await api.patch(`/api/veiculos/${id}/status`);
  } catch (e: any) {
    return e.response?.data;
  } finally {
    revalidatePath('/ui/veiculos');
  }
}