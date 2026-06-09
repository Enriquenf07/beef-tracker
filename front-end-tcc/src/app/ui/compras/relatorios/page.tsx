import { createApi } from "@/app/lib/api"
import RelatorioComprasClient from "./RelatorioComprasClient"

export default async function RelatorioCompras() {
    const api = await createApi()

    let pedidos: any[] = []
    let fornecedores: any[] = []

    try {
        const { data } = await api.get('/compras/pedido') as any
        pedidos = data ?? []
    } catch { pedidos = [] }

    try {
        const { data } = await api.get('/compras/fornecedor') as any
        fornecedores = data ?? []
    } catch { fornecedores = [] }

    return <RelatorioComprasClient pedidos={pedidos} fornecedores={fornecedores} />
}