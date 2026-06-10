import { createApi } from "@/app/lib/api"
import Content from "./components/Content"

export default async function Viagens(props: any) {
    const api = await createApi()
    let viagens = []
    let totalPages = 0
    let veiculos = []
    let sensores = []
    let motoristas = []

    const searchParams = await props?.searchParams

    try {
        const query: Record<string, any> = {}
        if (searchParams?.chave) query.chave = searchParams.chave
        if (searchParams?.status && searchParams.status !== 'null') query.status = searchParams.status
        if (searchParams?.page) query.page = searchParams.page

        const { data } = await api.get('/viagem', { params: query }) as any
        viagens = data.content || []
        totalPages = data.pages || 0
    } catch { viagens = [] }

    try {
        const { data } = await api.get('/api/veiculos') as any
        veiculos = data ?? []
    } catch { veiculos = [] }

    try {
        const { data } = await api.get('/api/sensor') as any
        sensores = data ?? []
    } catch { sensores = [] }

    try {
        const { data } = await api.get('/usuario/motoristas') as any
        motoristas = data ?? []
    } catch { motoristas = [] }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content
                viagens={viagens}
                totalPages={totalPages}
                veiculos={veiculos}
                sensores={sensores}
                motoristas={motoristas}
            />
        </div>
    )
}