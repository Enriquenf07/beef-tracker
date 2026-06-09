import { createApi } from "@/app/lib/api"
import Content from "./components/Content"

export default async function Clientes(props: any) {
    const api = await createApi()
    let clientes = []
    const searchParams = await props?.searchParams

    try {
        const query: Record<string, any> = {}

        if (searchParams?.chave) {
            query.chave = searchParams.chave
        }

        if (searchParams?.status && searchParams.status !== 'null') {
            query.status = searchParams.status
        }

        const { data } = await api.get('/vendas/cliente', { params: query }) as any
        clientes = data
    } catch (e) {
        clientes = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content clientes={clientes} />
        </div>
    )
}