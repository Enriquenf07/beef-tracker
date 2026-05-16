import { createApi } from "@/app/lib/api"
import Content from "./components/Content"

export default async function Pedidos(props: any) {
    const api = await createApi()

    let pedidos = []

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

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content pedidos={pedidos} />
        </div>
    )
}