import { createApi } from "@/app/lib/api"



export default async function ViagensAlertas(props: any) {
    const api = await createApi()
    let viagens = []
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
        viagens = data
    } catch (e) {
        viagens = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <p>A</p>
        </div>
    )
}