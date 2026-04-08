'use client';

import { useState } from "react";
import { Plus, Search, Edit } from "lucide-react";
import ModalCadastro from "./ModalCadastro";

export default function Content({ pedidos }: { pedidos: any[] }) {
    const [isModalOpen, setIsModalOpen] = useState(false);

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h1 className="text-2xl font-bold text-gray-800">Pedidos de Compra</h1>
                <button
                    onClick={() => setIsModalOpen(true)}
                    className="bg-[#1e293b] hover:bg-[#334155] text-white px-4 py-2 rounded flex items-center gap-2 text-sm font-medium"
                >
                    <Plus size={16} /> Cadastrar
                </button>
            </div>

            { }

            { }
            {isModalOpen && <ModalCadastro onClose={() => setIsModalOpen(false)} />}
        </div>
    );
}