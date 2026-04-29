
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
        chave: searchParams?.chave,
        status: searchParams?.status,
        page: searchParams?.page ? Number(searchParams.page) : 0
    }
    try {
        const { data } = await api.get('/usuario', {
            params: {...params, status: params.status != 'null' ? params.status : null}
        }) as any
        console.log(data)
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