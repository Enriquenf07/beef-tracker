'use client'

import { useState, useTransition } from "react"
import { useSearchParams } from "next/navigation"
import { Plus, Search, PenBox } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import Page from "@/app/components/CrudPage"
import { handleCadastroVeiculo, handleInativarVeiculo } from '../action'

export default function Content({ veiculos }: { veiculos: any[] }) {
    const [open, setOpen] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [form, setForm] = useState<any>({})
    const [isPending, startTransition] = useTransition()

    const searchParams = useSearchParams()
    const chave = searchParams.get('chave')
    const status = searchParams.get('status')

    const onHandleCadastro = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        const formData = new FormData(e.currentTarget)
        startTransition(async () => {
            const erro = await handleCadastroVeiculo(formData)
            if (erro) setError(erro.detail)
            else setOpen(false)
        })
    }

    const onHandleInativar = async () => {
        startTransition(async () => {
            const erro = await handleInativarVeiculo(form?.metadata.id)
            if (erro) setError(erro.detail)
            else setOpen(false)
        })
    }

    return (
        <Page.Content>
            <Page.Header>
                <Page.Title>Gestão de Veículos</Page.Title>
                <Page.Modal>
                    <Dialog open={open} onOpenChange={(v) => { if (!v) setForm({}); setOpen(v); }}>
                        <DialogTrigger asChild>
                            <Button><Plus className="mr-2 h-4 w-4" /> Cadastrar Veículo</Button>
                        </DialogTrigger>
                        <DialogContent>
                            <DialogHeader>
                                <DialogTitle>{!form?.metadata?.id ? 'Novo Veículo' : 'Editar Veículo'}</DialogTitle>
                            </DialogHeader>
                            <form onSubmit={onHandleCadastro} className="flex flex-col gap-3">
                                <input hidden name="id" defaultValue={form?.metadata?.id} />
                                <Input placeholder="Placa (ex: ABC1D23)" name="placa" defaultValue={form?.data?.placa} required />
                                <div className="flex gap-2">
                                    <Input placeholder="Marca" name="marca" defaultValue={form?.data?.marca} required />
                                    <Input placeholder="Modelo" name="modelo" defaultValue={form?.data?.modelo} required />
                                </div>
                                <div className="flex gap-2">
                                    <Input placeholder="Ano" name="ano" type="number" defaultValue={form?.data?.ano} />
                                    <Input placeholder="Capacidade (KG)" name="capacidadeCarga" type="number" defaultValue={form?.data?.capacidadeCarga} />
                                </div>
                                <Button type="submit" disabled={isPending}>Salvar</Button>
                                {form?.metadata?.id && (
                                    <Button
                                        type="button"
                                        variant={form?.data?.ativo ? "destructive" : "outline"}
                                        onClick={onHandleInativar}
                                    >
                                        {form?.data?.ativo ? 'Inativar Veículo' : 'Reativar Veículo'}
                                    </Button>
                                )}
                            </form>
                        </DialogContent>
                    </Dialog>
                </Page.Modal>
            </Page.Header>

            <Page.Filter>
                <form className="flex gap-3">
                    <Input className="w-64" name="chave" defaultValue={chave || ""} placeholder="Placa ou Modelo" />
                    <Select defaultValue={status || "null"} name="status">
                        <SelectTrigger className="w-40"><SelectValue placeholder="Status" /></SelectTrigger>
                        <SelectContent>
                            <SelectItem value="null">Todos</SelectItem>
                            <SelectItem value="true">Ativos</SelectItem>
                            <SelectItem value="false">Inativos</SelectItem>
                        </SelectContent>
                    </Select>
                    <Button type="submit"><Search /></Button>
                </form>
            </Page.Filter>

            <Page.Table>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Status</TableHead>
                            <TableHead>Placa</TableHead>
                            <TableHead>Veículo</TableHead>
                            <TableHead>Capacidade</TableHead>
                            <TableHead className="w-10"></TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {veiculos.map((v: any) => (
                            <TableRow key={v.metadata.id}>
                                <TableCell>
                                    <span className={`px-2 py-1 rounded-full text-xs ${v.data.ativo ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}`}>
                                        {v.data.ativo ? 'Ativo' : 'Inativo'}
                                    </span>
                                </TableCell>
                                <TableCell className="font-mono font-bold">{v.data.placa}</TableCell>
                                <TableCell>{v.data.marca} {v.data.modelo} ({v.data.ano})</TableCell>
                                <TableCell>{v.data.capacidadeCarga} kg</TableCell>
                                <TableCell>
                                    <Button variant="ghost" size="icon" onClick={() => { setForm(v); setOpen(true); }}>
                                        <PenBox className="h-4 w-4" />
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Page.Table>
        </Page.Content>
    )
}