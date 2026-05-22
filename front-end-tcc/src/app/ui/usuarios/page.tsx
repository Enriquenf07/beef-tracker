
import { createApi } from "@/app/lib/api"
import Content from "./Content"



export default async function Usuarios(props: any) {
    const api = await createApi()
    let usuarios = []
    let totalPages = 0
    let roles = []
    const searchParams = await props?.searchParams
    console.log(searchParams)
    const params = {
        ...(searchParams?.chave ? { chave: searchParams.chave } : {}),
        ...(searchParams?.status && searchParams.status !== 'null' ? { status: searchParams.status } : {}),
        page: searchParams?.page ? Number(searchParams.page) : 1
    }
    try {
        console.log('params enviados:', params)
        const { data } = await api.get('/usuario', { params }) as any
        usuarios = data.content || []
        totalPages = data.pages || 0
    } catch (e) {
        usuarios = []
    }

    try {
        const { data } = await api.get('/usuario/roles') as any
        roles = data.roles
    } catch (e) {
        roles = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content usuarios={usuarios} roles={roles} totalPages={totalPages} />
        </div>
    )
}