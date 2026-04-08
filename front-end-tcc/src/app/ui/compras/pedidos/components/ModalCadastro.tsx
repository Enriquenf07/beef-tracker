'use client';

import { X } from "lucide-react";
import { useState } from "react";
import { handleCadastroPedido } from "../action";

export default function ModalCadastro({ onClose }: { onClose: () => void }) {
    const [loading, setLoading] = useState(false);

    async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();
        setLoading(true);
        const formData = new FormData(e.currentTarget);

        const data = {
            fornecedorId: Number(formData.get('fornecedorId')),
            valorTotal: Number(formData.get('valorTotal')),
            dataEntrega: formData.get('dataEntrega'),
            observacao: formData.get('observacao'),
            status: 'RASCUNHO'
        };

        const res = await handleCadastroPedido(data);
        if (res.success) onClose();
        else alert(res.error);
        setLoading(false);
    }

    return (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
            <div className="bg-[#fdf8f3] w-full max-w-md rounded-2xl shadow-xl overflow-hidden">
                <div className="flex justify-between items-center p-6 border-b border-orange-100">
                    <h2 className="text-xl font-bold text-gray-800">Novo Pedido</h2>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X size={20} /></button>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-4">
                    <input name="fornecedorId" placeholder="ID do Fornecedor" required
                        className="w-full p-3 rounded-xl border border-orange-200 focus:outline-none focus:ring-2 focus:ring-orange-500/20 bg-white" />

                    <div className="grid grid-cols-2 gap-4">
                        <input name="valorTotal" type="number" step="0.01" placeholder="Valor Total" required
                            className="w-full p-3 rounded-xl border border-orange-200 focus:outline-none focus:ring-2 focus:ring-orange-500/20 bg-white" />
                        <input name="dataEntrega" type="date" required
                            className="w-full p-3 rounded-xl border border-orange-200 focus:outline-none focus:ring-2 focus:ring-orange-500/20 bg-white text-gray-500" />
                    </div>

                    <textarea name="observacao" placeholder="Observações" rows={3}
                        className="w-full p-3 rounded-xl border border-orange-200 focus:outline-none focus:ring-2 focus:ring-orange-500/20 bg-white" />

                    <button type="submit" disabled={loading}
                        className="w-full py-3 bg-[#1e293b] text-white rounded-xl font-bold hover:bg-[#334155] transition-all disabled:opacity-50">
                        {loading ? 'Salvando...' : 'Salvar'}
                    </button>
                </form>
            </div>
        </div>
    );
}