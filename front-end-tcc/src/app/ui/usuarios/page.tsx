
import { createApi } from "@/app/lib/api"
import Content from "./Content"



export default async function Fornecedores(props: any) {
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
        usuarios = data || []
        const {data: dataRoles} = await api.get('/usuario/roles') as any
        roles = dataRoles?.roles || []
    } catch (e) {
        usuarios = []
    }

    return (
        <div className="flex flex-col gap-3 justify-start">
            <Content usuarios={usuarios} roles={roles}/>
        </div>
    )
}