import { createApi } from "@/app/lib/api"
import Content from "./components/Content"
import { LeituraStats } from "./components/Stats"


export default async function Viagens(props: any) {
    const api = await createApi()
    let itens = [] as any
    let stats = {} as LeituraStats
    const { id } = await props?.params
    console.log("ID: ", id)
    try {
        const {data} = await api.get(`/viagem/${id}/leituras`) as any
        itens = data.leituras
        const res = await api.get(`/viagem/${id}/stats`) as any
        stats = res.data
        console.log(stats)
    } catch (e) {
        console.error("Erro status:", e?.response?.status)
        console.error("Erro message:", e?.message)
        console.error("Erro data:", e?.response?.data)
        itens = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content leituras={itens} stats={stats} />
        </div>
    )
}