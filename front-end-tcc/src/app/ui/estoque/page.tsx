import { createApi } from "@/app/lib/api"
import Content from "./Content"
<<<<<<< Updated upstream

export default async function Estoque(props: any) {
    const api = await createApi()

    let lotesBrutos: any[] = []
    let pedidosCompra: any[] = []
    let fornecedores: any[] = []

=======
export default async function Estoque(props: any) {
    const api = await createApi()
 
    let lotesBrutos: any[] = []
    let lotesFracionados: any[] = []
    let pedidosCompra: any[] = []
    let pedidosVenda: any[] = []
    let fornecedores: any[] = []
    let clientes: any[] = []
 
>>>>>>> Stashed changes
    try {
        const { data } = await api.get('/compras/pedido') as any
        pedidosCompra = data || []
    } catch {
        pedidosCompra = []
    }
<<<<<<< Updated upstream

    try {
        const lotesPromises = pedidosCompra.map((p: any) =>
            api
                .get(`/compras/pedido/${p.metadata.id}/lote`)
                .then(({ data }: any) => data || [])
                .catch(() => [])
        )
        const resultados = await Promise.all(lotesPromises)
=======
 
    try {
        const { data } = await api.get('/vendas/pedido') as any
        pedidosVenda = data || []
    } catch {
        pedidosVenda = []
    }
 
    try {
        const resultados = await Promise.all(
            pedidosCompra.map((p: any) =>
                api
                    .get(`/compras/pedido/${p.metadata.id}/lote`)
                    .then(({ data }: any) => data || [])
                    .catch(() => [])
            )
        )
>>>>>>> Stashed changes
        lotesBrutos = resultados.flat()
    } catch {
        lotesBrutos = []
    }
<<<<<<< Updated upstream

    try {
        const { data } = await api.get('/fornecedor') as any
=======
 
    try {
        console.log(pedidosVenda)
        const resultados = await Promise.all(
            pedidosVenda.map((p: any) =>
                api
                    .get(`/vendas/pedido/${p.metadata.id}/lote`)
                    .then(({ data }: any) => data || [])
                    .catch(() => [])
            )
        )
        lotesFracionados = resultados.flat()
    } catch {
        lotesFracionados = []
    }
 
    try {
        const { data } = await api.get('compras/fornecedor') as any
>>>>>>> Stashed changes
        fornecedores = data || []
    } catch {
        fornecedores = []
    }
<<<<<<< Updated upstream

=======
 
    try {
        const { data } = await api.get('vendas/cliente') as any
        clientes = data || []
    } catch {
        clientes = []
    }
 
>>>>>>> Stashed changes
    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content
                lotesBrutos={lotesBrutos}
<<<<<<< Updated upstream
                pedidosCompra={pedidosCompra}
                fornecedores={fornecedores}
            />
        </div>
    )
}
=======
                lotesFracionados={lotesFracionados}
                pedidosCompra={pedidosCompra}
                pedidosVenda={pedidosVenda}
                fornecedores={fornecedores}
                clientes={clientes}
            />
        </div>
    )
}
 
>>>>>>> Stashed changes
