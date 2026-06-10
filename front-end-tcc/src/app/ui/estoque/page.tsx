import { createApi } from "@/app/lib/api"
import Content from "./Content"
export default async function Estoque(props: any) {
    const api = await createApi()
 
    let lotesBrutos: any[] = []
    let lotesFracionados: any[] = []
    let pedidosCompra: any[] = []
    let pedidosVenda: any[] = []
    let fornecedores: any[] = []
    let clientes: any[] = []
 
    try {
        const { data } = await api.get('/compras/pedido') as any
        pedidosCompra = data || []
    } catch {
        pedidosCompra = []
    }
 
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
        lotesBrutos = resultados.flat()
    } catch {
        lotesBrutos = []
    }
 
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
        fornecedores = data || []
    } catch {
        fornecedores = []
    }
 
    try {
        const { data } = await api.get('vendas/cliente') as any
        clientes = data || []
    } catch {
        clientes = []
    }
 
    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content
                lotesBrutos={lotesBrutos}
                lotesFracionados={lotesFracionados}
                pedidosCompra={pedidosCompra}
                pedidosVenda={pedidosVenda}
                fornecedores={fornecedores}
                clientes={clientes}
            />
        </div>
    )
}
 
