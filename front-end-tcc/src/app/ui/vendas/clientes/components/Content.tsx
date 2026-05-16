'use client'

import { Button } from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import { PenBox, Plus, Search } from "lucide-react"
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
import { useSearchParams } from "next/navigation"
import Page from "@/app/components/CrudPage"
import { InputCpfCnpj } from "./InputCpfCnpj"
import { maskCpfCnpjPrivate } from "@/app/lib/masks"

export default function Content(props: any) {
    const [open, setOpen] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [form, setForm] = useState<any>({})
    const searchParams = useSearchParams()
    const status = searchParams.get('status')
    const chave = searchParams.get('chave')
    const [isPending, startTransition] = useTransition()

    const onHandleInativar = async () => {
        startTransition(async () => {
            const erro = await handleInativar(form?.metadata.id) as any
            if (erro) setError(erro.detail)
            setOpen(false)
        })
    }

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
                <Page.Title>Clientes</Page.Title>
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
                                    {!form?.metadata?.id ? 'Cadastrar Cliente' : 'Editar Cliente'}
                                </DialogTitle>
                            </DialogHeader>
                            {isPending ? <p>Carregando...</p> : (
                                <>
                                    <form onSubmit={onHandleCadastro} className="flex flex-col gap-2">
                                        <input hidden name="id" defaultValue={form?.metadata?.id} />
                                        <Input
                                            placeholder="Nome"
                                            name="nome"
                                            type="text"
                                            defaultValue={form?.data?.nome}
                                        />
                                        <Input
                                            placeholder="Apelido"
                                            name="apelido"
                                            type="text"
                                            defaultValue={form?.data?.apelido}
                                        />
                                        { }
                                        <InputCpfCnpj
                                            name="cpfCnpj"
                                            defaultValue={form?.data?.cpfCnpj}
                                        />
                                        <Input
                                            placeholder="E-mail"
                                            name="email"
                                            type="email"
                                            defaultValue={form?.data?.email}
                                        />
                                        <Input
                                            placeholder="Telefone"
                                            name="telefone"
                                            type="text"
                                            defaultValue={form?.data?.telefone}
                                        />
                                        <Input
                                            placeholder="CEP"
                                            name="cep"
                                            type="text"
                                            defaultValue={form?.data?.cep}
                                        />
                                        <Input
                                            placeholder="UF (ex: SP)"
                                            name="uf"
                                            type="text"
                                            maxLength={2}
                                            defaultValue={form?.data?.uf}
                                        />
                                        <Button type="submit">Salvar</Button>
                                    </form>
                                    {form?.metadata?.id && (
                                        <Button
                                            className={
                                                form?.data?.ativo
                                                    ? "bg-destructive hover:bg-destructive-hover text-white"
                                                    : "bg-accent hover:bg-accent-hover text-white"
                                            }
                                            onClick={onHandleInativar}>
                                            {form?.data?.ativo ? 'Inativar' : 'Ativar'}
                                        </Button>
                                    )}
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
                                <SelectItem value="true">Ativos</SelectItem>
                                <SelectItem value="false">Inativos</SelectItem>
                            </SelectGroup>
                        </SelectContent>
                    </Select>
                    <Button type="submit"><Search /></Button>
                </form>
            </Page.Filter>
            <Page.Table>
                {props.clientes.length > 0 ? (
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead className="w-30">Ativo/Inativo</TableHead>
                                <TableHead>Nome</TableHead>
                                <TableHead>Apelido</TableHead>
                                <TableHead>CPF/CNPJ</TableHead>
                                <TableHead>E-mail</TableHead>
                                <TableHead>Telefone</TableHead>
                                <TableHead>CEP</TableHead>
                                <TableHead>UF</TableHead>
                                <TableHead></TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {props.clientes.map((c: any) => (
                                <TableRow key={c.metadata.id}>
                                    <TableCell>
                                        <div className={c.data.ativo
                                            ? 'p-1 flex justify-center items-center rounded-xl border bg-blue-200'
                                            : 'p-1 flex justify-center items-center rounded-xl border bg-muted'}>
                                            {c.data.ativo ? 'Ativo' : 'Inativo'}
                                        </div>
                                    </TableCell>
                                    <TableCell className="font-medium">{c.data.nome}</TableCell>
                                    <TableCell>{c.data.apelido}</TableCell>
                                    {/* Exibe CPF/CNPJ anonimizado na tabela (LGPD) */}
                                    <TableCell>{maskCpfCnpjPrivate(c.data.cpfCnpj)}</TableCell>
                                    <TableCell>{c.data.email}</TableCell>
                                    <TableCell>{c.data.telefone}</TableCell>
                                    <TableCell>{c.data.cep}</TableCell>
                                    <TableCell>{c.data.uf}</TableCell>
                                    <TableCell>
                                        <Button className="bg-secondary" onClick={() => {
                                            setForm(c)
                                            setOpen(prev => !prev)
                                        }}>
                                            <PenBox />
                                        </Button>
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