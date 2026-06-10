import { createApi } from "@/app/lib/api"
import Content from "./components/Content"

export default async function PedidosVenda(props: any) {
    const api = await createApi()

    let pedidos = []
    let clientes = []
    let lotesBrutos = []
    let viagens = []

    const searchParams = await props?.searchParams

    try {
        const query: Record<string, any> = {}
        if (searchParams?.clienteId) query.clienteId = searchParams.clienteId
        if (searchParams?.status && searchParams.status !== 'null') query.status = searchParams.status

        const { data } = await api.get('/vendas/pedido', { params: query }) as any
        pedidos = data
    } catch (e) {
        pedidos = []
    }

    try {
        const { data } = await api.get('/vendas/cliente') as any
        clientes = data
    } catch (e) {
        clientes = []
    }

    try {
        const { data } = await api.get('/compras/pedido', { params: { status: 'ENTREGUE' } }) as any
        const pedidosEntregues = data ?? []
        const lotes = await Promise.all(
            pedidosEntregues.map(async (p: any) => {
                try {
                    const res = await api.get(`/compras/pedido/${p.metadata.id}/lote`) as any
                    return res.data ?? []
                } catch { return [] }
            })
        )
        lotesBrutos = lotes.flat()
    } catch (e) {
        lotesBrutos = []
    }

    try {
        const { data } = await api.get('/viagem/pendentes') as any
        viagens = data
    } catch (e) {
        viagens = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content pedidos={pedidos} clientes={clientes} lotesBrutos={lotesBrutos} viagens={viagens} />
        </div>
    )
}