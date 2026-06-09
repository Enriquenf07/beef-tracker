"use server"

import { createApi } from "../lib/api";

export async function finalizarCadastroAction(formData: { senha: string; token: string | null }) {
  try {
    const token = formData.token;
    if(!token) {
      return { success: false, message: "Token de cadastro não encontrado." };
    }
    const api = await createApi();
    const response = await api.patch(`/usuario/finalizar`, formData);
    return { success: true };
  } catch (error) {
    return { success: false, message: "Falha na conexão com o servidor." };
  }
}