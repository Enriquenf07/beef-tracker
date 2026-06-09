import { createApi } from "@/app/lib/api"
import Content from "./componetns/Content"

export default async function SensoresPage(props: any) {
    const api = await createApi()
    let sensores = []
    const searchParams = await props?.searchParams

    const params: Record<string, string> = {}
    if (searchParams?.chave) params.chave = searchParams.chave
    if (searchParams?.status && searchParams.status !== 'null') params.status = searchParams.status

    try {
        const { data } = await api.get('/api/sensor', { params }) as any
        sensores = data
    } catch (e) {
        sensores = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content sensores={sensores} />
        </div>
    )
}