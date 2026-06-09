'use client'

import { useState, useTransition } from "react"
import { useSearchParams } from "next/navigation"
import { Plus, Search, PenBox, Trash2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import Page from "@/app/components/CrudPage"
import { handleCadastroSensor, handleAlterarStatusSensor, handleExcluirSensor } from '../action'

export default function Content({ sensores }: { sensores: any[] }) {
    const [open, setOpen] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [form, setForm] = useState<any>({})
    const [confirmDelete, setConfirmDelete] = useState(false)
    const [isPending, startTransition] = useTransition()

    const searchParams = useSearchParams()
    const chave = searchParams.get('chave')
    const status = searchParams.get('status')

    const onHandleCadastro = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        const formData = new FormData(e.currentTarget)
        startTransition(async () => {
            const erro = await handleCadastroSensor(formData)
            if (erro) setError(erro.detail)
            else { setOpen(false); setError(null) }
        })
    }

    const onHandleAlterarStatus = () => {
        startTransition(async () => {
            await handleAlterarStatusSensor(form.metadata.id)
            setOpen(false)
        })
    }

    const onHandleExcluir = () => {
        startTransition(async () => {
            const erro = await handleExcluirSensor(form.metadata.id) as any
            if (erro) setError(erro.detail)
            setOpen(false)
        })
    }

    return (
        <Page.Content>
            <Page.Header>
                <Page.Title>Gestão de Sensores</Page.Title>
                <Page.Modal>
                    <Dialog open={open} onOpenChange={(v) => { if (!v) { setForm({}); setError(null); setConfirmDelete(false) } setOpen(v) }}>
                        <DialogTrigger asChild>
                            <Button><Plus className="mr-2 h-4 w-4" /> Cadastrar Sensor</Button>
                        </DialogTrigger>
                        <DialogContent>
                            <DialogHeader>
                                <DialogTitle>{!form?.metadata?.id ? 'Novo Sensor' : 'Editar Sensor'}</DialogTitle>
                            </DialogHeader>
                            <form onSubmit={onHandleCadastro} className="flex flex-col gap-3">
                                <input hidden name="id" defaultValue={form?.metadata?.id} />
                                <Input
                                    placeholder="Descrição (ex: Sensor Câmara Fria 01)"
                                    name="descricao"
                                    defaultValue={form?.data?.descricao}
                                    required
                                />
                                {error && <p className="text-sm text-red-500">{error}</p>}
                                <Button type="submit" disabled={isPending}>Salvar</Button>
                                {form?.metadata?.id && (
                                    <Button
                                        type="button"
                                        variant={form?.data?.ativo ? "destructive" : "outline"}
                                        onClick={onHandleAlterarStatus}
                                        disabled={isPending}
                                    >
                                        {form?.data?.ativo ? 'Inativar Sensor' : 'Reativar Sensor'}
                                    </Button>
                                )}
                                {form?.metadata?.id && (
                                    confirmDelete ? (
                                        <div className="flex gap-2">
                                            <Button
                                                type="button"
                                                variant="destructive"
                                                className="flex-1"
                                                onClick={() => { setConfirmDelete(false); onHandleExcluir() }}
                                            >
                                                Confirmar exclusão
                                            </Button>
                                            <Button
                                                type="button"
                                                variant="outline"
                                                className="flex-1"
                                                onClick={() => setConfirmDelete(false)}
                                            >
                                                Cancelar
                                            </Button>
                                        </div>
                                    ) : (
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            className="text-destructive hover:text-destructive hover:bg-destructive/10"
                                            onClick={() => setConfirmDelete(true)}
                                        >
                                            <Trash2 className="mr-2 h-4 w-4" /> Excluir sensor
                                        </Button>
                                    )
                                )}
                            </form>
                        </DialogContent>
                    </Dialog>
                </Page.Modal>
            </Page.Header>

            <Page.Filter>
                <form className="flex gap-3">
                    <Input className="w-64" name="chave" defaultValue={chave || ""} placeholder="Descrição" />
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
                            <TableHead>ID</TableHead>
                            <TableHead>Descrição</TableHead>
                            <TableHead className="w-10"></TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {sensores.map((s: any) => (
                            <TableRow key={s.metadata.id}>
                                <TableCell>
                                    <span className={`px-2 py-1 rounded-full text-xs ${s.data.ativo ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}`}>
                                        {s.data.ativo ? 'Ativo' : 'Inativo'}
                                    </span>
                                </TableCell>
                                <TableCell className="font-mono text-sm text-muted-foreground">#{s.metadata.id}</TableCell>
                                <TableCell>{s.data.descricao}</TableCell>
                                <TableCell>
                                    <Button variant="ghost" size="icon" onClick={() => { setForm(s); setOpen(true) }}>
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