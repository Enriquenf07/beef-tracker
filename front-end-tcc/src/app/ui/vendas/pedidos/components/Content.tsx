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
    SelectValue,
} from "@/components/ui/select"
import { Box, Check, PenBox, Search, Trash } from "lucide-react"
import { useState, useTransition } from "react"
import { useSearchParams } from "next/navigation"
import Page from "@/app/components/CrudPage"
import { handleCadastro, handleLoteCadastro, handleAtualizarStatus } from "../action"
import { ModalLote, ModalPedido } from "./Modal"

const STATUS_CORES: Record<string, string> = {
    PENDENTE: 'bg-yellow-100 text-yellow-800',
    ENTREGUE: 'bg-green-100 text-green-800',
    CANCELADO: 'bg-red-100 text-red-800',
    RASCUNHO: 'bg-gray-100 text-gray-600',
}

export default function Content(props: any) {
    const [open, setOpen] = useState(false)
    const [openLote, setOpenLote] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [form, setForm] = useState<any>({})
    const [clienteId, setClienteId] = useState<string>('')
    const [isPending, startTransition] = useTransition()
    const searchParams = useSearchParams()
    const status = searchParams.get('status')
    const clientes: any[] = props.clientes ?? []
    const lotesBrutos: any[] = props.lotesBrutos ?? []

    const onHandleCadastro = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        const formData = new FormData(e.currentTarget)
        formData.set('clienteId', clienteId)
        startTransition(async () => {
            const erro = await handleCadastro(formData) as any
            if (erro) { setError(erro.detail); return }
            setOpen(false)
            setClienteId('')
        })
    }

    const onHandleLote = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        const formData = new FormData(e.currentTarget)
        startTransition(async () => {
            const erro = await handleLoteCadastro(form?.metadata?.id, formData) as any
            if (erro) { setError(erro.detail); return }
            setOpenLote(false)
        })
    }

    const onHandleStatus = async (id: number, status: string) => {
        startTransition(async () => {
            const erro = await handleAtualizarStatus(id, status) as any
            if (erro) setError(erro.detail)
        })
    }

    return (
        <Page.Content>
            <Page.Header>
                <Page.Title>Pedidos de Venda</Page.Title>
                <Page.Modal>
                    <ModalPedido
                        open={open}
                        setOpen={setOpen}
                        form={form}
                        setForm={setForm}
                        clienteId={clienteId}
                        setClienteId={setClienteId}
                        clientes={clientes}
                        isPending={isPending}
                        onHandleCadastro={onHandleCadastro}
                    />
                    <ModalLote
                        open={openLote}
                        setOpen={setOpenLote}
                        pedidoId={form?.metadata?.id ?? null}
                        lotesBrutos={lotesBrutos}
                        isPending={isPending}
                        onHandleLote={onHandleLote}
                    />
                </Page.Modal>
            </Page.Header>

            <Page.Filter>
                {error && (
                    <div className="p-3 bg-red-100 rounded-md flex gap-2 items-center mb-2">
                        <button onClick={() => setError(null)} className="text-xs">X</button>
                        <p>{error}</p>
                    </div>
                )}
                <form className="flex gap-3">
                    <Select defaultValue={status || undefined} name="status">
                        <SelectTrigger className="w-52">
                            <SelectValue placeholder="Status" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectGroup>
                                <SelectItem value="null">Todos</SelectItem>
                                <SelectItem value="PENDENTE">Pendente</SelectItem>
                                <SelectItem value="ENTREGUE">Entregue</SelectItem>
                                <SelectItem value="CANCELADO">Cancelado</SelectItem>
                                <SelectItem value="RASCUNHO">Rascunho</SelectItem>
                            </SelectGroup>
                        </SelectContent>
                    </Select>
                    <Button type="submit"><Search /></Button>
                </form>
            </Page.Filter>

            <Page.Table>
                {props.pedidos.length > 0 ? (
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>ID</TableHead>
                                <TableHead>Cliente</TableHead>
                                <TableHead>Valor</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead>Data Venda</TableHead>
                                <TableHead>Vencimento</TableHead>
                                <TableHead></TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {props.pedidos.map((p: any) => {
                                const cliente = clientes.find((c: any) => c.metadata.id === p.data.clienteId)
                                return (
                                    <TableRow key={p.metadata.id}>
                                        <TableCell>#{p.metadata.id}</TableCell>
                                        <TableCell>
                                            {cliente ? cliente.data.nome : `#${p.data.clienteId}`}
                                        </TableCell>
                                        <TableCell>
                                            R$ {Number(p.data.valorTotal).toFixed(2)}
                                        </TableCell>
                                        <TableCell>
                                            <div className={`px-2 py-1 text-xs rounded-xl border text-center ${STATUS_CORES[p.data.status] ?? 'bg-muted'}`}>
                                                {p.data.status}
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            {p.data.dataVenda
                                                ? new Date(p.data.dataVenda).toLocaleDateString('pt-BR')
                                                : '-'}
                                        </TableCell>
                                        <TableCell>
                                            {p.data.dataVencimento
                                                ? new Date(p.data.dataVencimento).toLocaleDateString('pt-BR')
                                                : '-'}
                                        </TableCell>
                                        <TableCell className="flex gap-2">
                                            <Button
                                                className="bg-secondary text-black hover:text-white"
                                                onClick={() => {
                                                    setClienteId(String(p.data.clienteId))
                                                    setForm(p)
                                                    setOpen(true)
                                                }}
                                            >
                                                <PenBox size={16} />
                                            </Button>

                                            {p.data.status === 'PENDENTE' && (
                                                <Button
                                                    className="bg-amber-500 hover:bg-amber-600 text-white"
                                                    onClick={() => {
                                                        setForm(p)
                                                        setOpenLote(true)
                                                    }}
                                                >
                                                    <Box size={16} />
                                                </Button>
                                            )}

                                            {p.data.status === 'PENDENTE' && (
                                                <Button
                                                    className="bg-green-600 hover:bg-green-700 text-white"
                                                    onClick={() => onHandleStatus(p.metadata.id, 'ENTREGUE')}
                                                >
                                                    <Check size={16} />
                                                </Button>
                                            )}

                                            {p.data.status === 'PENDENTE' && (
                                                <Button
                                                    className="bg-destructive hover:bg-destructive/90"
                                                    onClick={() => onHandleStatus(p.metadata.id, 'CANCELADO')}
                                                >
                                                    <Trash size={16} />
                                                </Button>
                                            )}
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