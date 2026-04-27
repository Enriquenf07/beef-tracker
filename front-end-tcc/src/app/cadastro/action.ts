"use server"

import { createApi } from "../lib/api";

export async function finalizarCadastroAction(formData: { senha: string; token: string }) {
  try {
    console.log(formData)
    const api = await createApi();
    const response = await api.patch(`/usuario/finalizar`, formData);
    return { success: true };
  } catch (error) {
    return { success: false, message: "Falha na conexão com o servidor." };
  }
}