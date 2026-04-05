
import { createApi } from "@/app/lib/api"
import Content from "./Content"



export default async function Usuarios(props: any) {
    const api = await createApi()
    let usuarios = []
    let roles = []
    const searchParams = await props?.searchParams
    const params = {
        chave: searchParams?.chave,
        status: searchParams?.status,
    }
    try {
        const { data } = await api.get('/usuario', {
            params: {...params, status: params.status != 'null' ? params.status : null}
        }) as any
        usuarios = data
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
            <Content usuarios={usuarios} roles={roles} />
        </div>
    )
}