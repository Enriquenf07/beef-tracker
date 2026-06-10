import { createApi } from "@/app/lib/api"
import Content from "./Content"

export default async function Estoque(props: any) {
    const api = await createApi()

    let lotesBrutos: any[] = []
    let pedidosCompra: any[] = []
    let fornecedores: any[] = []

    try {
        const { data } = await api.get('/compras/pedido') as any
        pedidosCompra = data || []
    } catch {
        pedidosCompra = []
    }

    try {
        const lotesPromises = pedidosCompra.map((p: any) =>
            api
                .get(`/compras/pedido/${p.metadata.id}/lote`)
                .then(({ data }: any) => data || [])
                .catch(() => [])
        )
        const resultados = await Promise.all(lotesPromises)
        lotesBrutos = resultados.flat()
    } catch {
        lotesBrutos = []
    }

    try {
        const { data } = await api.get('/fornecedor') as any
        fornecedores = data || []
    } catch {
        fornecedores = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content
                lotesBrutos={lotesBrutos}
                pedidosCompra={pedidosCompra}
                fornecedores={fornecedores}
            />
        </div>
    )
}