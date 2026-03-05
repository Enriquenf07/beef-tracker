
'use client'

import { Button } from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import { PenBox, Plus, Search } from "lucide-react"

import {
    Table,
    TableBody,
    TableCaption,
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
import Fornecedores from "../page"
import { useRouter } from "next/navigation"



export default function Content(props: any) {
    const [open, setOpen] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [form, setForm] = useState<any>({})
    const router = useRouter()
    const searchParams = useSearchParams()
    const status = searchParams.get('status')
    const chave = searchParams.get('chave')
    const [isPending, startTransition] = useTransition()

    const onHandleInativar = async () => {
        startTransition(async () => {
            const erro = await handleInativar(form?.metadata.id, !form?.ativo) as any
            if (erro) {
                setError(erro.detail)
            }
            setOpen(false)
        })
    }
    const onHandleCadastro = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        const formData = new FormData(e.currentTarget)
        startTransition(async () => {
            const erro =  await handleCadastro(formData) as any;
            if (erro) {
                setError(erro.detail)
            }
            setOpen(false)
        })
    }
    
    return (
        <>
            <div className="flex justify-between">
                <p className="font-bold text-2xl">Fornecedores</p>
                <div>
                    <Dialog open={open} onOpenChange={() => {
                        setForm({})
                        setOpen(prev => !prev)
                    }}>
                        <DialogTrigger>
                            <Button >
                                <Plus />
                                Cadastrar
                            </Button>
                        </DialogTrigger>
                        <DialogContent>
                            <DialogHeader>
                                <DialogTitle>{!form?.metadata?.id ? 'Cadastrar Fornecedor' : 'Editar Fornecedor'}</DialogTitle>

                            </DialogHeader>
                            {isPending ? <p>Carregando...</p> : (
                                <>
                                    <form onSubmit={onHandleCadastro} className="flex flex-col gap-2">
                                        <input hidden name="id" defaultValue={form?.metadata?.id} />
                                        <Input placeholder="Nome"
                                            name="nome" type="text" defaultValue={form?.data?.nome} />
                                        <Input placeholder="Apelido"
                                            name="apelido" type="text" defaultValue={form?.data?.apelido} />
                                        <Input placeholder="CNPJ"
                                            name="cnpj" type="text" defaultValue={form?.data?.cnpj} />
                                        <Input placeholder="CEP"
                                            name="cep" type="text" defaultValue={form?.data?.cep} />
                                        <Input placeholder="Endereço"
                                            name="endereco" type="text" defaultValue={form?.data?.endereco} />
                                        <Button type="submit">Salvar</Button>
                                    </form>
                                    {form?.metadata?.id && (
                                        <Button
                                            className={
                                                form?.data?.ativo
                                                    ? "bg-destructive hover:bg-destructive-hover text-white"
                                                    : "bg-accent hover:bg-accent-hover text-white"
                                            }
                                            onClick={onHandleInativar}>{form?.data?.ativo ? 'Inativar' : 'Ativar'}</Button>

                                    )}
                                </>
                            )}
                        </DialogContent>
                    </Dialog>
                </div>
            </div>
            <div>
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
                    <Button type="submit" ><Search /></Button>
                </form>
            </div>
            <div className="p-5 flex flex-col gap-3 justify-start">
                {props.fornecedores.length > 0 ? (
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead className="w-30">Ativo/Inativo</TableHead>
                                <TableHead>Nome</TableHead>
                                <TableHead>Apelido</TableHead>
                                <TableHead>Cnpj</TableHead>
                                <TableHead>Cep</TableHead>
                                <TableHead>Endereço</TableHead>
                                <TableHead></TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {props.fornecedores.map((f: any) => (
                                <TableRow key={f.metadata.id}>
                                    <TableCell>
                                        <div className={f.data.ativo ? 'p-1 flex justify-center items-center rounded-xl border bg-blue-200' : 'p-1 flex justify-center items-center rounded-xl border bg-muted '}>
                                            {f.data.ativo ? 'Ativo' : 'Inativo'}
                                        </div>
                                    </TableCell>
                                    <TableCell className="font-medium">{f.data.nome}</TableCell>
                                    <TableCell>{f.data.apelido}</TableCell>
                                    <TableCell>{f.data.cnpj}</TableCell>
                                    <TableCell>{f.data.cep}</TableCell>
                                    <TableCell>{f.data.endereco}</TableCell>
                                    <TableCell className="text-[#F1F5F9]">
                                        <Button className="bg-secondary" onClick={() => {
                                            setOpen(prev => !prev)
                                            setForm(f)
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

            </div>
        </>
    )
}