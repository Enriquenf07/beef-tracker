"use client";

import { handleCadastro } from "../actions";

export default function Content() {
    return (
        <div>
            <h1>Cadastro de Veículos</h1>

            <form action={handleCadastro}>
                <input name="placa" placeholder="Placa" required />
                <input name="modelo" placeholder="Modelo" required />
                <input name="marca" placeholder="Marca" />

                <input
                    name="ano"
                    type="number"
                    placeholder="Ano"
                />

                <input
                    name="capacidadeCarga"
                    type="number"
                    placeholder="Capacidade de carga"
                    required
                />

                <label>
                    Ativo:
                    <input type="checkbox" name="ativo" />
                </label>

                <button type="submit">Salvar</button>
            </form>
        </div>
    );
}