'use client'

import { Button } from "@/components/ui/button"



import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"

import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue
} from "@/components/ui/select"

import { Input } from "@/components/ui/input"

import {
    Box,
    Check,
    PenBox,
    Pin,
    Plus,
    Search,
    Trash
} from "lucide-react"

import {
    useState,
    useTransition
} from "react"

import { useSearchParams } from "next/navigation"

import Page from "@/app/components/CrudPage"

import {
    handleCadastro,
    handleAtualizarStatus,
    handleLoteCadastro,
    handleVincularViagem
} from "../action"
import { ModalLote, ModalPedido, ModalViagem } from "./Modal"

export default function Content(props: any) {
    const [open, setOpen] = useState(false)
    const [openLote, setOpenLote] = useState(false)
    const [openViagem, setOpenViagem] = useState(false)
    const [pedidoId, setPedidoId] = useState(null)

    const [error, setError] =
        useState<string | null>(null)

    const [form, setForm] = useState<any>({})

    const [fornecedorId, setFornecedorId] =
        useState<string>('')

    const [isPending, startTransition] =
        useTransition()

    const searchParams = useSearchParams()

    const status = searchParams.get('status')

    const fornecedores: any[] = props.fornecedores ?? []

    const onHandleCadastro = async (
        e: React.FormEvent<HTMLFormElement>
    ) => {
        e.preventDefault()

        const formData = new FormData(e.currentTarget)

        formData.set('fornecedorId', fornecedorId)

        startTransition(async () => {
            const erro = await handleCadastro(formData) as any
            if (erro) {
                setError(erro.detail)
                return
            }
            setOpen(false)
            setFornecedorId('')
        })
    }
    const onHandleCadastroLote = (e: React.FormEvent<HTMLFormElement>, id: any) => {
        e.preventDefault()
        const formData = new FormData(e.currentTarget)

        startTransition(async () => {
            const erro = await handleLoteCadastro(formData, id) as any
            if (erro) { setError(erro.detail); return }
            setOpen(false)
            setFornecedorId('')
        })
    }

    const onHandleCadastroViagem = (e: React.FormEvent<HTMLFormElement>, id: any) => {
        e.preventDefault()
        const formData = new FormData(e.currentTarget)

        startTransition(async () => {
            const erro = await handleVincularViagem(formData, id) as any
            if (erro) { setError(erro.detail); return }
            setOpen(false)
            setFornecedorId('')
        })
    }

    const onHandleStatus = async (
        id: number,
        status: string
    ) => {
        startTransition(async () => {
            const erro =
                await handleAtualizarStatus(id, status) as any

            if (erro) {
                setError(erro.detail)
            }
        })
    }

    return (
        <Page.Content>
            <Page.Header>
                <Page.Title>
                    Pedidos de Compra
                </Page.Title>

                <Page.Modal>
                    <ModalPedido
                        open={open}
                        setOpen={setOpen}
                        form={form}
                        setForm={setForm}
                        fornecedorId={fornecedorId}
                        setFornecedorId={setFornecedorId}
                        fornecedores={fornecedores}
                        isPending={isPending}
                        onHandleCadastro={onHandleCadastro}
                    />
                    <ModalViagem
                        open={openViagem}
                        form={form}
                        setOpen={setOpenViagem}
                        viagens={props.viagens}
                        pedidoId={pedidoId}
                        onHandleCadastro={onHandleCadastroViagem}
                    />
                </Page.Modal>
            </Page.Header>

            <Page.Filter>
                {error && (
                    <div className="p-3 bg-red-100 rounded-md flex gap-2 items-center mb-2">
                        <button
                            onClick={() => setError(null)}
                            className="text-xs"
                        >
                            X
                        </button>
                        <p>{error}</p>
                    </div>
                )}

                <form className="flex gap-3">
                    <Select
                        defaultValue={status || undefined}
                        name="status"
                    >
                        <SelectTrigger className="w-52">
                            <SelectValue placeholder="Status" />
                        </SelectTrigger>

                        <SelectContent>
                            <SelectGroup>
                                <SelectItem value="null">
                                    Todos
                                </SelectItem>

                                <SelectItem value="CANCELADO">
                                    Cancelado
                                </SelectItem>

                                <SelectItem value="ENTREGUE">
                                    Entregue
                                </SelectItem>
                            </SelectGroup>
                        </SelectContent>
                    </Select>

                    <Button type="submit">
                        <Search />
                    </Button>
                </form>
            </Page.Filter>

            <Page.Table>
                {props.pedidos.length > 0 ? (
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>ID</TableHead>
                                <TableHead>Fornecedor</TableHead>
                                <TableHead>Valor</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead></TableHead>
                                <TableHead></TableHead>
                                <TableHead></TableHead>
                            </TableRow>
                        </TableHeader>

                        <TableBody>
                            {props.pedidos.map((p: any) => {
                                const fornecedor = fornecedores.find(
                                    (f: any) => f.metadata.id === p.data.fornecedorId
                                )
                                return (
                                    <TableRow key={p.metadata.id}>
                                        <TableCell>
                                            {p.metadata.id}
                                        </TableCell>

                                        <TableCell>
                                            {fornecedor
                                                ? fornecedor.data.nome
                                                : `#${p.data.fornecedorId}`}
                                        </TableCell>

                                        <TableCell>
                                            R$ {p.data.valorTotal}
                                        </TableCell>

                                        <TableCell>
                                            <div className="p-1 flex justify-center items-center rounded-xl border bg-muted">
                                                {p.data.status}
                                            </div>
                                        </TableCell>

                                        <TableCell className="flex gap-2">
                                            <div className="p-1 flex justify-center items-center rounded-xl gap-3">


                                                {p.data.status == "PENDENTE" && (
                                                    <Button
                                                        className="bg-blue-600 hover:bg-blue-700 text-white"
                                                        onClick={() => {
                                                            setForm(p);
                                                            setPedidoId(p.metadata.id)
                                                            setOpenViagem(true);
                                                        }}
                                                    >
                                                        <Pin size={16} />
                                                    </Button>
                                                )}

                                                {p.data.status == "PENDENTE" && (
                                                    <Button
                                                        className="bg-amber-500 hover:bg-amber-600 text-white"
                                                        onClick={() => {
                                                            setForm(p);
                                                            setPedidoId(p.metadata.id)
                                                            setOpenLote(true);
                                                        }}
                                                    >
                                                        <Box size={16} />
                                                    </Button>
                                                )}

                                                {p.data.status == "PENDENTE" && (
                                                    <Button
                                                        className="bg-green-600 hover:bg-green-700 text-white"
                                                        onClick={() => onHandleStatus(p.metadata.id, "ENTREGUE")}
                                                    >
                                                        <Check size={16} />
                                                    </Button>
                                                )}

                                                {p.data.status == "PENDENTE" && (
                                                    <Button
                                                        className="bg-destructive hover:bg-destructive/90"
                                                        onClick={() => onHandleStatus(Number(p.data.id), "CANCELADO")}
                                                    >
                                                        <Trash size={16} />
                                                    </Button>
                                                )}
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                )
                            })}
                        </TableBody>
                    </Table>
                ) : (
                    <div className="flex justify-center items-center h-20 w-full">
                        <p>Nenhum item encontrado</p>
                    </div>
                )}
            </Page.Table>
        </Page.Content>
    )
}