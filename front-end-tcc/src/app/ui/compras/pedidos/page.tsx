import { createApi } from "@/app/lib/api";
import Content from "./components/Content";

export default async function PedidosPage(props: any) {
    const api = await createApi();
    let pedidos = [];


    const searchParams = await props?.searchParams;
    const params = {
        chave: searchParams?.chave,
        status: searchParams?.status,
    };

    try {

        const { data } = await api.get('/compras/pedido', {
            params: {
                ...params,
                status: params.status !== 'null' ? params.status : null
            }
        }) as any;

        pedidos = data;
    } catch (e) {
        console.error("Erro ao carregar pedidos:", e);
        pedidos = [];
    }

    return (
        <div className="flex flex-col gap-3 justify-start">

            <Content pedidos={pedidos} />
        </div>
    );
}