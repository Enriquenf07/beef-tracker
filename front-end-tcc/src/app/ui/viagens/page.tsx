import { createApi } from "@/app/lib/api"
import Content from "./components/Content"


export default async function Viagens(props: any) {
    const api = await createApi()
    let viagens = []
    let totalPages = 0
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
        const { data } = await api.get('/viagem', { params: query }) as any
        console.log('data recebido:', data)
        viagens = data.content || []
        totalPages = data.pages || 0
    } catch (e) {
        viagens = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content viagens={viagens} totalPages={totalPages} />
        </div>
    )
}