import { createApi } from "@/app/lib/api"
import Content from "../../../ui/viagens/[id]/components/Content"
import { LeituraStats } from "../../../ui/viagens/[id]/components/Stats"


export default async function PublicViagem(props: any) {
    const api = await createApi()
    let itens = [] as any
    let stats = {} as LeituraStats
    const { token } = await props?.params
    try {
        const {data} = await api.get(`/viagem/${token}/leituras`) as any
        itens = data.leituras
        const res = await api.get(`/viagem/${token}/stats`) as any
        stats = res.data
    } catch (e) {
        itens = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content leituras={itens} stats={stats} />
        </div>
    )
}