'use client'

import {
    BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer,
    PieChart, Pie, Cell, Legend
} from "recharts"

const STATUS_CORES: Record<string, string> = {
    PENDENTE: '#f59e0b',
    ENTREGUE: '#22c55e',
    CANCELADO: '#ef4444',
    RASCUNHO: '#94a3b8',
}

export default function RelatorioComprasClient({ pedidos, fornecedores }: { pedidos: any[], fornecedores: any[] }) {

    // Total geral
    const totalGeral = pedidos.reduce((acc, p) => acc + Number(p.data.valorTotal ?? 0), 0)
    const totalEntregue = pedidos.filter(p => p.data.status === 'ENTREGUE').reduce((acc, p) => acc + Number(p.data.valorTotal ?? 0), 0)
    const totalCancelado = pedidos.filter(p => p.data.status === 'CANCELADO').reduce((acc, p) => acc + Number(p.data.valorTotal ?? 0), 0)
    const totalPendente = pedidos.filter(p => p.data.status === 'PENDENTE').reduce((acc, p) => acc + Number(p.data.valorTotal ?? 0), 0)

    // Por status (pizza)
    const porStatus = Object.entries(
        pedidos.reduce((acc: Record<string, number>, p) => {
            const s = p.data.status ?? 'DESCONHECIDO'
            acc[s] = (acc[s] ?? 0) + 1
            return acc
        }, {})
    ).map(([name, value]) => ({ name, value }))

    // Por fornecedor (barras)
    const porFornecedor = fornecedores.map(f => {
        const pedidosForn = pedidos.filter(p => p.data.fornecedorId === f.metadata.id)
        const total = pedidosForn.reduce((acc, p) => acc + Number(p.data.valorTotal ?? 0), 0)
        return { nome: f.data.apelido || f.data.nome, total, qtd: pedidosForn.length }
    }).filter(f => f.qtd > 0).sort((a, b) => b.total - a.total)

    const fmt = (v: number) => `R$ ${v.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`

    return (
        <div className="flex flex-col gap-6 p-4">
            <h1 className="text-2xl font-bold">Relatório de Compras</h1>

            {/* Cards resumo */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {[
                    { label: 'Total Geral', valor: totalGeral, cor: 'bg-blue-100 text-blue-800' },
                    { label: 'Entregues', valor: totalEntregue, cor: 'bg-green-100 text-green-800' },
                    { label: 'Pendentes', valor: totalPendente, cor: 'bg-yellow-100 text-yellow-800' },
                    { label: 'Cancelados', valor: totalCancelado, cor: 'bg-red-100 text-red-800' },
                ].map(c => (
                    <div key={c.label} className={`rounded-xl p-4 ${c.cor}`}>
                        <p className="text-sm font-medium">{c.label}</p>
                        <p className="text-xl font-bold mt-1">{fmt(c.valor)}</p>
                        <p className="text-xs mt-1">{pedidos.filter(p =>
                            c.label === 'Total Geral' || p.data.status === c.label.toUpperCase().replace('ES', 'ES')
                        ).length} pedido(s)</p>
                    </div>
                ))}
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Pizza - por status */}
                <div className="bg-white rounded-xl border p-4">
                    <h2 className="font-semibold mb-4">Pedidos por Status</h2>
                    {porStatus.length > 0 ? (
                        <ResponsiveContainer width="100%" height={250}>
                            <PieChart>
                                <Pie data={porStatus} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={90} label={({ name, value }) => `${name}: ${value}`}>
                                    {porStatus.map((entry) => (
                                        <Cell key={entry.name} fill={STATUS_CORES[entry.name] ?? '#cbd5e1'} />
                                    ))}
                                </Pie>
                                <Legend />
                                <Tooltip />
                            </PieChart>
                        </ResponsiveContainer>
                    ) : <p className="text-center text-muted-foreground py-10">Sem dados</p>}
                </div>

                {/* Barras - valor por fornecedor */}
                <div className="bg-white rounded-xl border p-4">
                    <h2 className="font-semibold mb-4">Valor Total por Fornecedor</h2>
                    {porFornecedor.length > 0 ? (
                        <ResponsiveContainer width="100%" height={250}>
                            <BarChart data={porFornecedor} layout="vertical">
                                <XAxis type="number" tickFormatter={v => `R$ ${(v / 1000).toFixed(0)}k`} />
                                <YAxis type="category" dataKey="nome" width={120} tick={{ fontSize: 12 }} />
                                <Tooltip formatter={(v: any) => fmt(Number(v))} />
                                <Bar dataKey="total" fill="#1e3a5f" radius={[0, 4, 4, 0]} />
                            </BarChart>
                        </ResponsiveContainer>
                    ) : <p className="text-center text-muted-foreground py-10">Sem dados</p>}
                </div>
            </div>

            {/* Tabela detalhada */}
            <div className="bg-white rounded-xl border p-4">
                <h2 className="font-semibold mb-4">Detalhamento por Fornecedor</h2>
                <table className="w-full text-sm">
                    <thead>
                        <tr className="border-b text-left text-muted-foreground">
                            <th className="pb-2">Fornecedor</th>
                            <th className="pb-2">Qtd Pedidos</th>
                            <th className="pb-2">Valor Total</th>
                            <th className="pb-2">Entregues</th>
                            <th className="pb-2">Cancelados</th>
                        </tr>
                    </thead>
                    <tbody>
                        {porFornecedor.map(f => {
                            const forn = fornecedores.find(x => (x.data.apelido || x.data.nome) === f.nome)
                            const pedsForn = pedidos.filter(p => p.data.fornecedorId === forn?.metadata?.id)
                            return (
                                <tr key={f.nome} className="border-b last:border-0">
                                    <td className="py-2 font-medium">{f.nome}</td>
                                    <td className="py-2">{f.qtd}</td>
                                    <td className="py-2">{fmt(f.total)}</td>
                                    <td className="py-2 text-green-700">{pedsForn.filter(p => p.data.status === 'ENTREGUE').length}</td>
                                    <td className="py-2 text-red-700">{pedsForn.filter(p => p.data.status === 'CANCELADO').length}</td>
                                </tr>
                            )
                        })}
                        {porFornecedor.length === 0 && (
                            <tr><td colSpan={5} className="py-4 text-center text-muted-foreground">Nenhum dado disponível</td></tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    )
}