'use client'

import {
    PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer,
    LineChart, Line, XAxis, YAxis, CartesianGrid,
    BarChart, Bar,
} from 'recharts'

const STATUS_CORES: Record<string, string> = {
    RASCUNHO: '#94a3b8',
    ENVIADO: '#60a5fa',
    CONFIRMADO: '#34d399',
    FINALIZADO: '#a78bfa',
    CANCELADO: '#f87171',
    FATURADO: '#fbbf24',
    EM_TRANSITO: '#818cf8',
    ENTREGUE: '#10b981',
}

const CORES_BARRAS = {
    compras: '#60a5fa',
    vendas: '#34d399',
}

function formatarReal(valor: number) {
    return `R$ ${valor.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`
}

interface Props {
    dadosPizza: { status: string; total: number }[]
    dadosLinha: { mes: string; total: number }[]
    dadosBarras: { mes: string; compras: number; vendas: number }[]
}

export default function GraficosHome({ dadosPizza, dadosLinha, dadosBarras }: Props) {
    const semDados = (arr: any[]) => arr.length === 0

    return (
        <div className="flex flex-col gap-6">
            <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider">Gráficos</h2>

            <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">


                <div className="rounded-xl border border-gray-200 bg-white p-5">
                    <p className="text-sm font-semibold text-gray-700 mb-4">Pedidos de Compra por Status</p>
                    {semDados(dadosPizza) ? (
                        <Vazio />
                    ) : (
                        <ResponsiveContainer width="100%" height={260}>
                            <PieChart>
                                <Pie
                                    data={dadosPizza}
                                    dataKey="total"
                                    nameKey="status"
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={90}
                                    label={({ name, percent }) =>
                                        `${name} ${((percent ?? 0) * 100).toFixed(0)}%`
                                    }
                                >
                                    {dadosPizza.map((entry) => (
                                        <Cell
                                            key={entry.status}
                                            fill={STATUS_CORES[entry.status] ?? '#cbd5e1'}
                                        />
                                    ))}
                                </Pie>
                                <Tooltip
                                    formatter={(value) => [`${value} pedido(s)`, 'Total'] as any}
                                />
                            </PieChart>
                        </ResponsiveContainer>
                    )}
                </div>

                { }
                <div className="rounded-xl border border-gray-200 bg-white p-5">
                    <p className="text-sm font-semibold text-gray-700 mb-4">Valor de Vendas por Mês</p>
                    {semDados(dadosLinha) ? (
                        <Vazio />
                    ) : (
                        <ResponsiveContainer width="100%" height={260}>
                            <LineChart data={dadosLinha}>
                                <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
                                <XAxis dataKey="mes" tick={{ fontSize: 12 }} />
                                <YAxis
                                    tick={{ fontSize: 12 }}
                                    tickFormatter={(v: number) => `R$${(v / 1000).toFixed(0)}k`}
                                />
                                <Tooltip
                                    formatter={(value) => [formatarReal(Number(value)), 'Vendas'] as any}
                                />
                                <Line
                                    type="monotone"
                                    dataKey="total"
                                    stroke="#34d399"
                                    strokeWidth={2.5}
                                    dot={{ r: 4, fill: '#34d399' }}
                                    activeDot={{ r: 6 }}
                                />
                            </LineChart>
                        </ResponsiveContainer>
                    )}
                </div>

            </div>

            { }
            <div className="rounded-xl border border-gray-200 bg-white p-5">
                <p className="text-sm font-semibold text-gray-700 mb-4">Compras vs Vendas por Mês (R$)</p>
                {semDados(dadosBarras) ? (
                    <Vazio />
                ) : (
                    <ResponsiveContainer width="100%" height={280}>
                        <BarChart data={dadosBarras} barCategoryGap="30%">
                            <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
                            <XAxis dataKey="mes" tick={{ fontSize: 12 }} />
                            <YAxis
                                tick={{ fontSize: 12 }}
                                tickFormatter={(v: number) => `R$${(v / 1000).toFixed(0)}k`}
                            />
                            <Tooltip
                                formatter={(value, name) => [
                                    formatarReal(Number(value)),
                                    name === 'compras' ? 'Compras' : 'Vendas',
                                ] as any}
                            />
                            <Legend formatter={(v: string) => v === 'compras' ? 'Compras' : 'Vendas'} />
                            <Bar dataKey="compras" fill={CORES_BARRAS.compras} radius={[4, 4, 0, 0]} />
                            <Bar dataKey="vendas" fill={CORES_BARRAS.vendas} radius={[4, 4, 0, 0]} />
                        </BarChart>
                    </ResponsiveContainer>
                )}
            </div>

        </div>
    )
}

function Vazio() {
    return (
        <div className="flex justify-center items-center h-40 text-gray-400 text-sm">
            Sem dados suficientes para exibir
        </div>
    )
}