import { createApi } from "@/app/lib/api"
import Content from "./components/Content"

export default async function Fornecedores(props: any) {
    const api = await createApi()
    let fornecedores = []
    const searchParams = await props?.searchParams
    const params = {
        chave: searchParams?.chave,
        status: searchParams?.status,
    }
    try {
        const statusValue = params.status === 'null' || params.status === undefined ? null : params.status
        const query: Record<string, any> = {}
        if (params.chave) query.chave = params.chave
        if (statusValue !== null) query.status = statusValue

        const { data } = await api.get('/compras/fornecedor', { params: query }) as any
        fornecedores = data
    } catch (e) {
        fornecedores = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content fornecedores={fornecedores} />
        </div>
    )
}