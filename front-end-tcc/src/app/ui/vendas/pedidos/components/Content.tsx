'use client'

import { Button } from "@/components/ui/button"

import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"

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

import { Input } from "@/components/ui/input"

import {
    PenBox,
    Plus,
    Search,
} from "lucide-react"

import {
    useState,
    useTransition,
} from "react"

import { useSearchParams } from "next/navigation"

import Page from "@/app/components/CrudPage"

import {
    handleCadastro,
    handleAtualizarStatus,
} from "../action"

const STATUS_VENDA = [
    { value: "RASCUNHO", label: "Rascunho" },
    { value: "CONFIRMADO", label: "Confirmado" },
    { value: "FATURADO", label: "Faturado" },
    { value: "EM_TRANSITO", label: "Em Trânsito" },
    { value: "ENTREGUE", label: "Entregue" },
    { value: "CANCELADO", label: "Cancelado" },
]

const statusColor: Record<string, string> = {
    RASCUNHO: "bg-muted",
    CONFIRMADO: "bg-blue-200",
    FATURADO: "bg-amber-200",
    EM_TRANSITO: "bg-violet-200",
    ENTREGUE: "bg-emerald-200",
    CANCELADO: "bg-red-200",
}

export default function Content(props: any) {
    const [open, setOpen] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [form, setForm] = useState<any>({})
    const [isPending, startTransition] = useTransition()
    const searchParams = useSearchParams()
    const status = searchParams.get('status')

    const onHandleCadastro = async (
        e: React.FormEvent<HTMLFormElement>
    ) => {
        e.preventDefault()
        const formData = new FormData(e.currentTarget)
        startTransition(async () => {
            const erro = await handleCadastro(formData) as any
            if (erro) {
                setError(erro.detail)
                return
            }
            setOpen(false)
        })
    }

    const onHandleStatus = async (id: number, status: string) => {
        startTransition(async () => {
            const erro = await handleAtualizarStatus(id, status) as any
            if (erro) {
                setError(erro.detail)
            }
        })
    }

    return (
        <Page.Content>
            <Page.Header>
                <Page.Title>Pedidos de Venda</Page.Title>
                <Page.Modal>
                    <Dialog
                        open={open}
                        onOpenChange={() => {
                            setForm({})
                            setOpen(prev => !prev)
                        }}
                    >
                        <DialogTrigger>
                            <Button>
                                <Plus />
                                Cadastrar
                            </Button>
                        </DialogTrigger>

                        <DialogContent>
                            <DialogHeader>
                                <DialogTitle>
                                    {!form?.metadata?.id
                                        ? 'Cadastrar Pedido'
                                        : 'Editar Pedido'}
                                </DialogTitle>
                            </DialogHeader>

                            {isPending ? (
                                <p>Carregando</p>
                            ) : (
                                <form
                                    onSubmit={onHandleCadastro}
                                    className="flex flex-col gap-2"
                                >
                                    <input
                                        hidden
                                        name="id"
                                        defaultValue={form?.metadata?.id}
                                    />

                                    <Input
                                        placeholder="Cliente ID"
                                        name="clienteId"
                                        type="number"
                                        defaultValue={form?.data?.clienteId}
                                    />

                                    <Input
                                        placeholder="Valor Total"
                                        name="valorTotal"
                                        type="number"
                                        step="0.01"
                                        defaultValue={form?.data?.valorTotal}
                                    />

                                    <Input
                                        placeholder="Observação"
                                        name="observacao"
                                        type="text"
                                        defaultValue={form?.data?.observacao}
                                    />

                                    <Input
                                        placeholder="Data da Venda"
                                        name="dataVenda"
                                        type="datetime-local"
                                        defaultValue={form?.data?.dataVenda}
                                    />

                                    <Input
                                        placeholder="Data de Vencimento"
                                        name="dataVencimento"
                                        type="date"
                                        defaultValue={form?.data?.dataVencimento}
                                    />

                                    <Button type="submit">Salvar</Button>
                                </form>
                            )}
                        </DialogContent>
                    </Dialog>
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
                    <Select defaultValue={status || undefined} name="status">
                        <SelectTrigger className="w-52">
                            <SelectValue placeholder="Status" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectGroup>
                                <SelectItem value="null">Todos</SelectItem>
                                {STATUS_VENDA.map(s => (
                                    <SelectItem key={s.value} value={s.value}>
                                        {s.label}
                                    </SelectItem>
                                ))}
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
                            {props.pedidos.map((p: any) => (
                                <TableRow key={p.metadata.id}>
                                    <TableCell>#{p.metadata.id}</TableCell>
                                    <TableCell>{p.data.clienteId}</TableCell>
                                    <TableCell>
                                        R$ {Number(p.data.valorTotal).toFixed(2)}
                                    </TableCell>
                                    <TableCell>
                                        <div className={`p-1 flex justify-center items-center rounded-xl border ${statusColor[p.data.status] ?? 'bg-muted'}`}>
                                            {STATUS_VENDA.find(s => s.value === p.data.status)?.label ?? p.data.status}
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
                                            className="bg-secondary"
                                            onClick={() => {
                                                setOpen(prev => !prev)
                                                setForm(p)
                                            }}
                                        >
                                            <PenBox />
                                        </Button>

                                        <Select
                                            onValueChange={(value) =>
                                                onHandleStatus(p.metadata.id, value)
                                            }
                                        >
                                            <SelectTrigger className="w-40">
                                                <SelectValue placeholder="Alterar status" />
                                            </SelectTrigger>
                                            <SelectContent>
                                                {STATUS_VENDA.map(s => (
                                                    <SelectItem key={s.value} value={s.value}>
                                                        {s.label}
                                                    </SelectItem>
                                                ))}
                                            </SelectContent>
                                        </Select>
                                    </TableCell>
                                </TableRow>
                            ))}
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