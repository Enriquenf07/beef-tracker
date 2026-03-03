
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
        const { data } = await api.get('/compras/fornecedor', {
            params: {...params, status: params.status != 'null' ? params.status : null}
        }) as any
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