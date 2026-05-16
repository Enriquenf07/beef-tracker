import { createApi } from "@/app/lib/api"
import { Building2, Users, ShoppingBag, Store, Truck } from "lucide-react"
import Link from "next/link"
import GraficosHome from "./components/GraficosHome"

async function getDados(api: any) {
    const resultados = await Promise.allSettled([
        api.get('/compras/fornecedor'),
        api.get('/vendas/cliente'),
        api.get('/compras/pedido', { params: { page: 0 } }),
        api.get('/vendas/pedido', { params: { page: 0 } }),
        api.get('/api/veiculos'),
    ])

    const getValue = (r: PromiseSettledResult<any>) =>
        r.status === 'fulfilled' ? (r.value as any)?.data ?? [] : []

    const [fornecedores, clientes, pedidosCompra, pedidosVenda, veiculos] = resultados.map(getValue)

    return { fornecedores, clientes, pedidosCompra, pedidosVenda, veiculos }
}

function agruparPorStatus(pedidos: any[]) {
    const contagem: Record<string, number> = {}
    for (const p of pedidos) {
        const s = p.data?.status ?? 'DESCONHECIDO'
        contagem[s] = (contagem[s] ?? 0) + 1
    }
    return Object.entries(contagem).map(([status, total]) => ({ status, total }))
}

function agruparPorMes(pedidosCompra: any[], pedidosVenda: any[]) {
    const meses: Record<string, { mes: string, compras: number, vendas: number }> = {}

    for (const p of pedidosCompra) {
        const data = p.data?.dataEmissao
        if (!data) continue
        const mes = data.substring(0, 7) // "2026-01"
        if (!meses[mes]) meses[mes] = { mes, compras: 0, vendas: 0 }
        meses[mes].compras += Number(p.data?.valorTotal ?? 0)
    }

    for (const p of pedidosVenda) {
        const data = p.data?.dataVenda
        if (!data) continue
        const mes = data.substring(0, 7)
        if (!meses[mes]) meses[mes] = { mes, compras: 0, vendas: 0 }
        meses[mes].vendas += Number(p.data?.valorTotal ?? 0)
    }

    return Object.values(meses)
        .sort((a, b) => a.mes.localeCompare(b.mes))
        .map(m => ({
            ...m,
            mes: new Date(m.mes + '-01').toLocaleDateString('pt-BR', { month: 'short', year: '2-digit' }),
            compras: Number(m.compras.toFixed(2)),
            vendas: Number(m.vendas.toFixed(2)),
        }))
}

function valorPorMesVenda(pedidosVenda: any[]) {
    const meses: Record<string, number> = {}

    for (const p of pedidosVenda) {
        const data = p.data?.dataVenda
        if (!data) continue
        const mes = data.substring(0, 7)
        meses[mes] = (meses[mes] ?? 0) + Number(p.data?.valorTotal ?? 0)
    }

    return Object.entries(meses)
        .sort(([a], [b]) => a.localeCompare(b))
        .map(([mes, total]) => ({
            mes: new Date(mes + '-01').toLocaleDateString('pt-BR', { month: 'short', year: '2-digit' }),
            total: Number(total.toFixed(2)),
        }))
}

export default async function Ui() {
    const api = await createApi()
    const { fornecedores, clientes, pedidosCompra, pedidosVenda, veiculos } = await getDados(api)

    const metricas = [
        {
            label: "Fornecedores",
            valor: fornecedores.length,
            ativos: fornecedores.filter((f: any) => f.data?.ativo).length,
            icon: Building2,
            cor: "bg-blue-50 text-blue-700 border-blue-200",
            iconCor: "text-blue-500",
            path: "/ui/compras/fornecedores",
        },
        {
            label: "Clientes",
            valor: clientes.length,
            ativos: clientes.filter((c: any) => c.data?.ativo).length,
            icon: Users,
            cor: "bg-emerald-50 text-emerald-700 border-emerald-200",
            iconCor: "text-emerald-500",
            path: "/ui/vendas/clientes",
        },
        {
            label: "Pedidos de Compra",
            valor: pedidosCompra.length,
            ativos: pedidosCompra.filter((p: any) => !['CANCELADO', 'FINALIZADO'].includes(p.data?.status)).length,
            icon: ShoppingBag,
            cor: "bg-amber-50 text-amber-700 border-amber-200",
            iconCor: "text-amber-500",
            path: "/ui/compras/pedidos",
        },
        {
            label: "Pedidos de Venda",
            valor: pedidosVenda.length,
            ativos: pedidosVenda.filter((p: any) => !['CANCELADO', 'ENTREGUE'].includes(p.data?.status)).length,
            icon: Store,
            cor: "bg-violet-50 text-violet-700 border-violet-200",
            iconCor: "text-violet-500",
            path: "/ui/vendas/pedidos",
        },
        {
            label: "Veículos",
            valor: veiculos.length,
            ativos: veiculos.filter((v: any) => v.data?.ativo).length,
            icon: Truck,
            cor: "bg-rose-50 text-rose-700 border-rose-200",
            iconCor: "text-rose-500",
            path: "/ui/veiculos",
        },
    ]

    const dadosPizza = agruparPorStatus(pedidosCompra)
    const dadosLinha = valorPorMesVenda(pedidosVenda)
    const dadosBarras = agruparPorMes(pedidosCompra, pedidosVenda)

    return (
        <div className="flex flex-col gap-8 p-2">

            <div>
                <h1 className="text-2xl font-bold text-gray-800">Bem-vindo ao BeefTracker</h1>
                <p className="text-sm text-gray-500 mt-1">Visão geral do sistema</p>
            </div>

            { }
            <div>
                <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">Resumo</h2>
                <div className="grid grid-cols-2 xl:grid-cols-5 gap-4">
                    {metricas.map((m) => (
                        <Link key={m.label} href={m.path}
                            className={`rounded-xl border p-4 flex flex-col gap-2 ${m.cor} hover:opacity-80 transition-opacity`}>
                            <div className="flex items-center justify-between">
                                <span className="text-xs font-medium">{m.label}</span>
                                <m.icon size={16} className={m.iconCor} />
                            </div>
                            <p className="text-3xl font-bold">{m.valor}</p>
                            <p className="text-xs opacity-70">{m.ativos} ativo{m.ativos !== 1 ? 's' : ''}</p>
                        </Link>
                    ))}
                </div>
            </div>

            { }
            <GraficosHome
                dadosPizza={dadosPizza}
                dadosLinha={dadosLinha}
                dadosBarras={dadosBarras}
            />

        </div>
    )
}