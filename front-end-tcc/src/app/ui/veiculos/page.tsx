import { createApi } from "@/app/lib/api"
import Content from "./components/Content"

export default async function VeiculosPage(props: any) {
    const api = await createApi()
    let veiculos = []
    const searchParams = await props?.searchParams

    try {
        const query: Record<string, any> = {}

        if (searchParams?.chave) {
            query.chave = searchParams.chave
        }

        if (searchParams?.status && searchParams.status !== 'null') {
            query.status = searchParams.status
        }

        const { data } = await api.get('/api/veiculos', { params: query }) as any
        veiculos = data
    } catch (e) {
        veiculos = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content veiculos={veiculos} />
        </div>
    )
}