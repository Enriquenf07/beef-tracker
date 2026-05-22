'use client'

import { Button } from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import { MapPin, PackageCheck, PenBox, Play, Plus, Search, Truck, X } from "lucide-react"
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
import { useState, useTransition } from "react"
import { Input } from "@/components/ui/input"
import { handleCadastro, handleInativar } from '../action'
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { usePathname, useRouter, useSearchParams } from "next/navigation"
import Page from "@/app/components/CrudPage"
import { InputCnpj } from "./InputCnpj"
import { maskCpfCnpjPrivate } from "@/app/lib/masks"
import { handleEntregue } from "../action"
import { handleEmTransito } from "../action"
import { handleCancelar } from "../action"
import { Pagination, PaginationContent, PaginationItem, PaginationNext, PaginationPrevious } from "@/components/ui/pagination"

export default function Content(props: any) {
    const [open, setOpen] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [form, setForm] = useState<any>({})
    const searchParams = useSearchParams()
    const status = searchParams.get('status')
    const chave = searchParams.get('chave')
    const router = useRouter()
    const [isPending, startTransition] = useTransition()

    const onHandleEmTransito = async () => {
        startTransition(async () => {
            const erro = await handleEmTransito(form?.metadata.id) as any
            if (erro) setError(erro.detail)
            setOpen(false)
        })
    }

    const onHandleEntregue = async () => {
        startTransition(async () => {
            const erro = await handleEntregue(form?.metadata.id) as any
            if (erro) setError(erro.detail)
            setOpen(false)
        })
    }

    const onHandleCancelar = async () => {
        startTransition(async () => {
            const erro = await handleCancelar(form?.metadata.id) as any
            if (erro) setError(erro.detail)
            setOpen(false)
        })
    }

    const createPageURL = (pageNumber: number | string) => {
        const params = new URLSearchParams(searchParams);
        params.set('page', pageNumber.toString());
        return `${pathname}?${params.toString()}`;
    };
    const pathname = usePathname();
    const currentPage = Number(searchParams.get('page')) || 1;



    const onHandleCadastro = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        const formData = new FormData(e.currentTarget)
        startTransition(async () => {
            const erro = await handleCadastro(formData) as any
            if (erro) setError(erro.detail)
            setOpen(false)
        })
    }

    return (
        <Page.Content>
            <Page.Header>
                <Page.Title>Viagens</Page.Title>
                <Page.Modal>
                    <Dialog open={open} onOpenChange={() => {
                        setForm({})
                        setOpen(prev => !prev)
                    }}>
                        <DialogTrigger>
                            <Button>
                                <Plus />
                                Cadastrar
                            </Button>
                        </DialogTrigger>
                        <DialogContent>
                            <DialogHeader>
                                <DialogTitle>
                                    {!form?.metadata?.id ? 'Cadastrar Viagem' : 'Editar Viagem'}
                                </DialogTitle>
                            </DialogHeader>
                            {isPending ? <p>Carregando...</p> : (
                                <>
                                    <form onSubmit={onHandleCadastro} className="flex flex-col gap-2">
                                        <input hidden name="id" defaultValue={form?.metadata?.id} />
                                        {!form?.metadata?.id ? (
                                            <>
                                                <Input
                                                    placeholder="Veículo ID"
                                                    name="veiculoId"
                                                    type="number"
                                                    defaultValue={form?.data?.veiculoId}
                                                />
                                                <Input
                                                    placeholder="Sensor ID"
                                                    name="sensorId"
                                                    type="number"
                                                    defaultValue={form?.data?.sensorId}
                                                />
                                                <Input
                                                    placeholder="Motorista ID"
                                                    name="motoristaId"
                                                    type="number"
                                                    defaultValue={form?.data?.motoristaId}
                                                />
                                                <Input
                                                    placeholder="Saída Prevista"
                                                    name="saidaEm"
                                                    type="datetime-local"
                                                    defaultValue={form?.data?.saidaEm?.slice(0, 16)}
                                                />
                                            </>
                                        ) : null}
                                        <Input
                                            placeholder="Descrição"
                                            name="descricao"
                                            type="text"
                                            defaultValue={form?.data?.descricao}
                                        />
                                        <Button type="submit">Salvar</Button>
                                    </form>
                                </>
                            )}
                        </DialogContent>
                    </Dialog>
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
                    <Input className="w-1/5" name="chave" defaultValue={chave || undefined} type="text" placeholder="Pesquisar" />
                    <Select defaultValue={status || undefined} name="status">
                        <SelectTrigger className="w-45">
                            <SelectValue placeholder="Status" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectGroup>
                                <SelectItem value="null">Todos</SelectItem>
                                <SelectItem value="PENDENTE">Pendente</SelectItem>
                                <SelectItem value="EM_TRANSITO">Em Trânsito</SelectItem>
                                <SelectItem value="ENTREGUE">Entregue</SelectItem>
                                <SelectItem value="CANCELADA">Cancelada</SelectItem>
                            </SelectGroup>
                        </SelectContent>
                    </Select>
                    <Button type="submit"><Search /></Button>
                </form>
            </Page.Filter>
            <Page.Table>
                {props.viagens.length > 0 ? (
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead className="w-36">Status</TableHead>
                                <TableHead>Descrição</TableHead>
                                <TableHead>Veículo ID</TableHead>
                                <TableHead>Sensor ID</TableHead>
                                <TableHead>Motorista ID</TableHead>
                                <TableHead>Saída Prevista</TableHead>
                                <TableHead>Saída Real</TableHead>
                                <TableHead>Entregue Em</TableHead>
                                <TableHead>Atualizado Em</TableHead>
                                <TableHead></TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {props.viagens.map((v: any) => (
                                <TableRow key={v.metadata.id}>
                                    <TableCell>
                                        <div className={
                                            v.data.statusViagem === 'EM_TRANSITO'
                                                ? 'p-1 flex justify-center items-center rounded-xl border bg-blue-200'
                                                : v.data.statusViagem === 'ENTREGUE'
                                                    ? 'p-1 flex justify-center items-center rounded-xl border bg-green-200'
                                                    : v.data.statusViagem === 'CANCELADA'
                                                        ? 'p-1 flex justify-center items-center rounded-xl border bg-red-200'
                                                        : 'p-1 flex justify-center items-center rounded-xl border bg-muted'
                                        }>
                                            {v.data.statusViagem === 'EM_TRANSITO' ? 'Em Trânsito'
                                                : v.data.statusViagem === 'ENTREGUE' ? 'Entregue'
                                                    : v.data.statusViagem === 'CANCELADA' ? 'Cancelada'
                                                        : 'Pendente'}
                                        </div>
                                    </TableCell>
                                    <TableCell className="font-medium">{v.data.descricao}</TableCell>
                                    <TableCell>{v.data.veiculoId}</TableCell>
                                    <TableCell>{v.data.sensorId}</TableCell>
                                    <TableCell>{v.data.motoristaId || '—'}</TableCell>
                                    <TableCell>{v.data.saidaEm ? new Date(v.data.saidaEm).toLocaleString('pt-BR') : '—'}</TableCell>
                                    <TableCell>{v.data.saidaRealEm ? new Date(v.data.saidaRealEm).toLocaleString('pt-BR') : '—'}</TableCell>
                                    <TableCell>{v.data.entregueEm ? new Date(v.data.entregueEm).toLocaleString('pt-BR') : '—'}</TableCell>
                                    <TableCell>{v.data.atualizadoEm ? new Date(v.data.atualizadoEm).toLocaleString('pt-BR') : '—'}</TableCell>
                                    <TableCell>
                                        {v.data.statusViagem === 'PENDENTE' && (
                                            <div className="flex gap-2">
                                                <Button
                                                    className="bg-green-500 hover:bg-green-600 text-white"
                                                    onClick={() => {
                                                        startTransition(async () => {
                                                            const erro = await handleEmTransito(v.metadata.id) as any
                                                            console.log('erro:', erro)
                                                            if (erro) setError(erro.detail)
                                                        })
                                                    }}>
                                                    <Play />
                                                </Button>
                                                <Button
                                                    className="bg-destructive hover:bg-destructive-hover text-white"
                                                    onClick={() => {
                                                        startTransition(async () => {
                                                            const erro = await handleCancelar(v.metadata.id) as any
                                                            if (erro) setError(erro.detail)
                                                        })
                                                    }}>
                                                    <X />
                                                </Button>
                                                <Button className="bg-secondary" onClick={() => {
                                                    setOpen(prev => !prev)
                                                    setForm(v)
                                                }}>
                                                    <PenBox />
                                                </Button>
                                            </div>
                                        )}
                                        {v.data.statusViagem === 'EM_TRANSITO' && (
                                            <div className="flex gap-2">
                                                <Button
                                                    className="bg-blue-500 hover:bg-blue-600 text-white"
                                                    onClick={() => router.push(`/ui/viagens/${v.metadata.id}`)}>
                                                    <MapPin />
                                                </Button>
                                                <Button
                                                    className="bg-green-500 hover:bg-green-600 text-white"
                                                    onClick={() => {
                                                        startTransition(async () => {
                                                            const erro = await handleEntregue(v.metadata.id) as any

                                                            if (erro) setError(erro.detail)
                                                        })
                                                    }}>
                                                    <PackageCheck />
                                                </Button>
                                                <Button className="bg-secondary" onClick={() => {
                                                    setOpen(prev => !prev)
                                                    setForm(v)
                                                }}>
                                                    <PenBox />
                                                </Button>
                                            </div>
                                        )}
                                        {(v.data.statusViagem === 'ENTREGUE' || v.data.statusViagem === 'CANCELADA') && (
                                            <div className="flex gap-2">
                                                {v.data.statusViagem === 'ENTREGUE' && (
                                                    <Button
                                                        className="bg-blue-500 hover:bg-blue-600 text-white"
                                                        onClick={() => router.push(`/ui/viagens/${v.metadata.id}`)}>
                                                        <MapPin />
                                                    </Button>
                                                )}
                                                <Button className="bg-secondary" onClick={() => {
                                                    setOpen(prev => !prev)
                                                    setForm(v)
                                                }}>
                                                    <PenBox />
                                                </Button>
                                            </div>
                                        )}
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>

                ) : (
                    <div className="flex justify-center items-center h-20 w-full">
                        <p>Nenhuma viagem encontrada</p>
                    </div>
                )}
                <Pagination>
                    <PaginationContent>
                        <PaginationItem>
                            <PaginationPrevious
                                href={createPageURL(currentPage - 1)}
                                aria-disabled={currentPage <= 1}
                                className={currentPage <= 1 ? "pointer-events-none opacity-50" : ""}
                            />
                        </PaginationItem>

                        <PaginationItem>
                            <span className="text-sm px-4">
                                Página <strong>{currentPage}</strong> de {props.totalPages}
                            </span>
                        </PaginationItem>

                        <PaginationItem>
                            <PaginationNext
                                href={createPageURL(props.currentPage + 1)}
                                aria-disabled={props.currentPage >= props.totalPages}
                                className={props.currentPage >= props.totalPages ? "pointer-events-none opacity-50" : ""}
                            />
                        </PaginationItem>
                    </PaginationContent>
                </Pagination>
            </Page.Table>
        </Page.Content>
    )
}