import { createApi } from "@/app/lib/api"
import Content from "./components/Content"

export default async function Pedidos(props: any) {
    const api = await createApi()

    let pedidos = []
    let fornecedores = []
    let viagens = []

    const searchParams = await props?.searchParams

    const params = {
        fornecedorId: searchParams?.fornecedorId,
        status: searchParams?.status,
    }

    try {
        const statusValue =
            params.status === 'null' ||
                params.status === undefined
                ? null
                : params.status

        const query: Record<string, any> = {}

        if (params.fornecedorId) {
            query.fornecedorId = params.fornecedorId
        }

        if (statusValue !== null) {
            query.status = statusValue
        }

        const { data } = await api.get(
            '/compras/pedido',
            { params: query }
        ) as any

        pedidos = data
    } catch (e) {
        pedidos = []
    }

    try {
        const { data } = await api.get('/compras/fornecedor') as any
        fornecedores = data
    } catch (e) {
        fornecedores = []
    }

    try {
        const { data } = await api.get('/viagem/pendentes') as any
        viagens = data
    } catch (e) {
        viagens = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content pedidos={pedidos} fornecedores={fornecedores} viagens={viagens} />
        </div>
    )
}