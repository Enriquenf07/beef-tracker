'use client'

import { Button } from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import { Plus } from "lucide-react"
import { useEffect, useState } from "react"

interface ModalPedidoProps {
    open: boolean
    setOpen: React.Dispatch<React.SetStateAction<boolean>>
    form: any
    setForm: React.Dispatch<React.SetStateAction<any>>
    clienteId: string
    setClienteId: React.Dispatch<React.SetStateAction<string>>
    clientes: any[]
    isPending: boolean
    onHandleCadastro: (e: React.FormEvent<HTMLFormElement>) => void
}

export function ModalPedido({
    open,
    setOpen,
    form,
    setForm,
    clienteId,
    setClienteId,
    clientes = [],
    isPending,
    onHandleCadastro,
}: ModalPedidoProps) {
    return (
        <Dialog
            open={open}
            onOpenChange={() => {
                setForm({})
                setClienteId('')
                setOpen(prev => !prev)
            }}
        >
            <DialogTrigger asChild>
                <Button>
                    <Plus />
                    Cadastrar Pedido
                </Button>
            </DialogTrigger>

            <DialogContent>
                <DialogHeader>
                    <DialogTitle>
                        {!form?.metadata?.id ? 'Cadastrar Pedido' : 'Editar Pedido'}
                    </DialogTitle>
                </DialogHeader>

                {isPending ? (
                    <p>Carregando...</p>
                ) : (
                    <form onSubmit={onHandleCadastro} className="flex flex-col gap-2">
                        <input hidden name="id" defaultValue={form?.metadata?.id} />

                        <Select
                            value={clienteId}
                            onValueChange={(val) => setClienteId(val)}
                        >
                            <SelectTrigger>
                                <SelectValue placeholder="Selecione um cliente" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectGroup>
                                    {clientes.length === 0 ? (
                                        <SelectItem value="__none" disabled>
                                            Nenhum cliente cadastrado
                                        </SelectItem>
                                    ) : (
                                        clientes.filter(c => c.data.ativo).map((c: any) => (
                                            <SelectItem
                                                key={c.metadata.id}
                                                value={String(c.metadata.id)}
                                            >
                                                {c.data.nome}
                                                {c.data.apelido ? ` (${c.data.apelido})` : ''}
                                            </SelectItem>
                                        ))
                                    )}
                                </SelectGroup>
                            </SelectContent>
                        </Select>

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
    )
}

interface ModalLoteProps {
    open: boolean
    setOpen: React.Dispatch<React.SetStateAction<boolean>>
    pedidoId: number | null
    lotesBrutos: any[]
    isPending?: boolean
    onHandleLote: (e: React.FormEvent<HTMLFormElement>) => void
}

export function ModalLote({
    open,
    setOpen,
    pedidoId,
    lotesBrutos = [],
    isPending,
    onHandleLote,
}: ModalLoteProps) {
    return (
        <Dialog open={open} onOpenChange={() => setOpen(prev => !prev)}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Cadastrar Lote Fracionado</DialogTitle>
                </DialogHeader>

                {isPending ? (
                    <p>Carregando...</p>
                ) : (
                    <form onSubmit={onHandleLote} className="flex flex-col gap-2">
                        <input hidden name="pedidoId" value={pedidoId ?? ''} readOnly />

                        <Select name="loteOriginalId">
                            <SelectTrigger>
                                <SelectValue placeholder="Selecione o lote original" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectGroup>
                                    {lotesBrutos.length === 0 ? (
                                        <SelectItem value="__none" disabled>
                                            Nenhum lote disponível
                                        </SelectItem>
                                    ) : (
                                        lotesBrutos.map((l: any) => (
                                            <SelectItem
                                                key={l.metadata.id}
                                                value={String(l.metadata.id)}
                                            >
                                                {l.data.nome} — {l.data.peso} kg
                                            </SelectItem>
                                        ))
                                    )}
                                </SelectGroup>
                            </SelectContent>
                        </Select>

                        <Input
                            placeholder="Nome do lote"
                            name="nome"
                            type="text"
                            required
                        />

                        <Input
                            placeholder="Descrição"
                            name="descricao"
                            type="text"
                        />

                        <Input
                            placeholder="Peso (kg)"
                            name="peso"
                            type="number"
                            required
                        />

                        <Button type="submit">Salvar</Button>
                    </form>
                )}
            </DialogContent>
        </Dialog>
    )
}

interface ModalViagemProps {
    open: boolean
    setOpen: React.Dispatch<React.SetStateAction<boolean>>
    viagens: any[]
    pedidoId: number | null
    form: any
    onHandleCadastro: (e: React.FormEvent<HTMLFormElement>, id: any) => void
}

export function ModalViagem({
    open,
    setOpen,
    viagens = [],
    pedidoId,
    form,
    onHandleCadastro,
}: ModalViagemProps) {
    const [viagemId, setViagemId] = useState<string>()

    useEffect(() => {
        setViagemId(String(form?.data?.viagemId ?? ""))
    }, [form?.data?.viagemId])

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Selecionar Viagem</DialogTitle>
                </DialogHeader>

                <form onSubmit={(e) => onHandleCadastro(e, pedidoId)} className="flex flex-col gap-4 py-4">
                    <Select
                        value={viagemId}
                        onValueChange={(val) => setViagemId(val)}
                        name="viagemId"
                    >
                        <SelectTrigger>
                            <SelectValue placeholder="Selecione uma viagem" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectGroup>
                                {viagens.length === 0 ? (
                                    <SelectItem value="__none" disabled>
                                        Nenhuma viagem cadastrada
                                    </SelectItem>
                                ) : (
                                    viagens.map((v: any) => (
                                        <SelectItem
                                            key={v.metadata.id}
                                            value={String(v.metadata.id)}
                                        >
                                            {v.metadata.id} - {v.data.descricao || "Sem descrição"}
                                        </SelectItem>
                                    ))
                                )}
                            </SelectGroup>
                        </SelectContent>
                    </Select>

                    <input type="hidden" name="viagemId" value={viagemId} />

                    <Button type="submit" disabled={!viagemId || viagemId === "__none"}>
                        Confirmar
                    </Button>
                </form>
            </DialogContent>
        </Dialog>
    )
}