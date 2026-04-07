import { createApi } from "@/app/lib/api"
import Content from "./components/Content"

export default async function VeiculosPage(props: any) {
    const api = await createApi()
    let veiculos = []
    const searchParams = await props?.searchParams

    const params = {
        chave: searchParams?.chave,
        status: searchParams?.status !== 'null' ? searchParams?.status : null,
    }

    try {
        const { data } = await api.get('/api/veiculos', { params }) as any
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