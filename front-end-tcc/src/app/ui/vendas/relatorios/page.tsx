import { createApi } from "@/app/lib/api"
import RelatorioVendasClient from "./RelatorioVendasClient"

export default async function RelatorioVendas() {
    const api = await createApi()

    let pedidos: any[] = []
    let clientes: any[] = []

    try {
        const { data } = await api.get('/vendas/pedido') as any
        pedidos = data ?? []
    } catch { pedidos = [] }

    try {
        const { data } = await api.get('/vendas/cliente') as any
        clientes = data ?? []
    } catch { clientes = [] }

    return <RelatorioVendasClient pedidos={pedidos} clientes={clientes} />
}