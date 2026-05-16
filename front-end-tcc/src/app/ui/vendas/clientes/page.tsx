import { createApi } from "@/app/lib/api"
import Content from "./components/Content"

export default async function Clientes(props: any) {
    const api = await createApi()
    let clientes = []
    const searchParams = await props?.searchParams
    const params = {
        chave: searchParams?.chave,
        status: searchParams?.status,
    }
    try {
        const { data } = await api.get('/vendas/cliente', {
            params: { ...params, status: params.status != 'null' ? params.status : null }
        }) as any
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