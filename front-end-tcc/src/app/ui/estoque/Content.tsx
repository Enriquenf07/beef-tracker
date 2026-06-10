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
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"

import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"

import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue
} from "@/components/ui/select"

import {
    Tabs,
    TabsContent,
    TabsList,
    TabsTrigger,
} from "@/components/ui/tabs"

import {
    Box,
    Plus,
    Scissors,
    Trash,
    Package,
    PackageOpen,
} from "lucide-react"

import { useState, useTransition } from "react"

import Page from "@/app/components/CrudPage"

import {
    handleCadastroLoteBruto,
    handleCadastroLoteFracionado,
    handleDeleteLoteBruto,
    handleDeleteLoteFracionado,
} from "./action"

// ---------------------------------------------------------------------------
// Tipos
// ---------------------------------------------------------------------------

interface LoteBruto {
    metadata: { id: number; token: string }
    data: {
        nome: string
        descricao?: string
        peso?: number
        pedidoCompraId: number
        criadoEm: string
    }
}

interface LoteFracionado {
    metadata: { id: number; token: string }
    data: {
        nome: string
        descricao?: string
        peso?: number
        loteOriginalId: number
        pedidoVendaId: number
        criadoEm: string
    }
}

interface Props {
    lotesBrutos: LoteBruto[]
    lotesFracionados: LoteFracionado[]
    pedidosCompra: { metadata: { id: number }; data: { fornecedorId: number } }[]
    pedidosVenda: { metadata: { id: number }; data: { clienteId: number; status: string } }[]
    fornecedores: { metadata: { id: number }; data: { nome: string } }[]
    clientes: { metadata: { id: number }; data: { nome: string } }[]
}

// ---------------------------------------------------------------------------
// Modal — Novo Lote Bruto
// ---------------------------------------------------------------------------

function ModalLoteBruto({
    open,
    setOpen,
    pedidosCompra,
    fornecedores,
    isPending,
    onSubmit,
}: {
    open: boolean
    setOpen: (v: boolean) => void
    pedidosCompra: Props['pedidosCompra']
    fornecedores: Props['fornecedores']
    isPending: boolean
    onSubmit: (e: React.FormEvent<HTMLFormElement>) => void
}) {
    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button className="gap-2">
                    <Plus size={16} />
                    Novo Lote Bruto
                </Button>
            </DialogTrigger>

            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Cadastrar Lote Bruto</DialogTitle>
                </DialogHeader>

                <form onSubmit={onSubmit} className="flex flex-col gap-4 mt-2">
                    <div className="flex flex-col gap-1">
                        <Label htmlFor="nome">Nome</Label>
                        <Input id="nome" name="nome" placeholder="Ex: Lote A – Carga 01" required />
                    </div>

                    <div className="flex flex-col gap-1">
                        <Label htmlFor="descricao">Descrição</Label>
                        <Textarea id="descricao" name="descricao" placeholder="Observações opcionais" />
                    </div>

                    <div className="flex flex-col gap-1">
                        <Label htmlFor="peso">Peso (kg)</Label>
                        <Input id="peso" name="peso" type="number" min={0} placeholder="0" />
                    </div>

                    <div className="flex flex-col gap-1">
                        <Label htmlFor="pedidoCompraId">Pedido de Compra</Label>
                        <Select name="pedidoCompraId" required>
                            <SelectTrigger>
                                <SelectValue placeholder="Selecione um pedido" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectGroup>
                                    {pedidosCompra?.map((p) => {
                                        const forn = fornecedores.find(
                                            (f) => f.metadata.id === p.data.fornecedorId
                                        )
                                        return (
                                            <SelectItem
                                                key={p.metadata.id}
                                                value={String(p.metadata.id)}
                                            >
                                                #{p.metadata.id} – {forn?.data.nome ?? `Fornecedor #${p.data.fornecedorId}`}
                                            </SelectItem>
                                        )
                                    })}
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    </div>

                    <Button type="submit" disabled={isPending} className="mt-2">
                        {isPending ? 'Salvando…' : 'Cadastrar'}
                    </Button>
                </form>
            </DialogContent>
        </Dialog>
    )
}

// ---------------------------------------------------------------------------
// Modal — Novo Lote Fracionado
// ---------------------------------------------------------------------------

function ModalLoteFracionado({
    open,
    setOpen,
    lotesBrutos,
    pedidosVenda,
    clientes,
    isPending,
    onSubmit,
    loteOriginalIdPreset,
}: {
    open: boolean
    setOpen: (v: boolean) => void
    lotesBrutos: LoteBruto[]
    pedidosVenda: Props['pedidosVenda']
    clientes: Props['clientes']
    isPending: boolean
    onSubmit: (e: React.FormEvent<HTMLFormElement>) => void
    loteOriginalIdPreset?: number | null
}) {
    return (
        <Dialog open={open} onOpenChange={setOpen}>


            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Cadastrar Lote Fracionado</DialogTitle>
                </DialogHeader>

                <form onSubmit={onSubmit} className="flex flex-col gap-4 mt-2">
                    <div className="flex flex-col gap-1">
                        <Label htmlFor="nome">Nome</Label>
                        <Input id="nome" name="nome" placeholder="Ex: Fração 01 – Lote A" required />
                    </div>

                    <div className="flex flex-col gap-1">
                        <Label htmlFor="descricao">Descrição</Label>
                        <Textarea id="descricao" name="descricao" placeholder="Observações opcionais" />
                    </div>

                    <div className="flex flex-col gap-1">
                        <Label htmlFor="peso">Peso (kg)</Label>
                        <Input id="peso" name="peso" type="number" min={0} placeholder="0" />
                    </div>

                    <div className="flex flex-col gap-1">
                        <Label htmlFor="loteOriginalId">Lote Bruto de Origem</Label>
                        <Select
                            name="loteOriginalId"
                            defaultValue={loteOriginalIdPreset ? String(loteOriginalIdPreset) : undefined}
                            required
                        >
                            <SelectTrigger>
                                <SelectValue placeholder="Selecione o lote bruto" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectGroup>
                                    {lotesBrutos?.map((l) => (
                                        <SelectItem key={l.metadata.id} value={String(l.metadata.id)}>
                                            #{l.metadata.id} – {l.data.nome}
                                        </SelectItem>
                                    ))}
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    </div>

                    <div className="flex flex-col gap-1">
                        <Label htmlFor="pedidoVendaId">Pedido de Venda</Label>
                        <Select name="pedidoVendaId" required>
                            <SelectTrigger>
                                <SelectValue placeholder="Selecione um pedido de venda" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectGroup>
                                    {pedidosVenda?.map((p) => {
                                        const cliente = clientes.find(
                                            (c) => c.metadata.id === p.data.clienteId
                                        )
                                        return (
                                            <SelectItem
                                                key={p.metadata.id}
                                                value={String(p.metadata.id)}
                                            >
                                                #{p.metadata.id} – {cliente?.data.nome ?? `Cliente #${p.data.clienteId}`}
                                            </SelectItem>
                                        )
                                    })}
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    </div>

                    <Button type="submit" disabled={isPending} className="mt-2">
                        {isPending ? 'Salvando…' : 'Cadastrar'}
                    </Button>
                </form>
            </DialogContent>
        </Dialog>
    )
}

// ---------------------------------------------------------------------------
// Componente principal
// ---------------------------------------------------------------------------

export default function Content(props: Props) {
    const [openBruto, setOpenBruto] = useState(false)
    const [openFracionado, setOpenFracionado] = useState(false)
    const [loteOriginalIdPreset, setLoteOriginalIdPreset] = useState<number | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [isPending, startTransition] = useTransition()

    const {
        lotesBrutos,
        lotesFracionados,
        pedidosCompra,
        pedidosVenda,
        fornecedores,
        clientes,
    } = props

    // ---- handlers ----------------------------------------------------------

    const onCadastrarBruto = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        const formData = new FormData(e.currentTarget)
        startTransition(async () => {
            const erro = await handleCadastroLoteBruto(formData) as any
            if (erro) { setError(erro.detail); return }
            setOpenBruto(false)
        })
    }

    const onCadastrarFracionado = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        const formData = new FormData(e.currentTarget)
        startTransition(async () => {
            const erro = await handleCadastroLoteFracionado(formData) as any
            if (erro) { setError(erro.detail); return }
            setOpenFracionado(false)
            setLoteOriginalIdPreset(null)
        })
    }

    const onDeleteBruto = (id: number) => {
        startTransition(async () => {
            const erro = await handleDeleteLoteBruto(id) as any
            if (erro) setError(erro.detail)
        })
    }

    const onDeleteFracionado = (id: number) => {
        startTransition(async () => {
            const erro = await handleDeleteLoteFracionado(id) as any
            if (erro) setError(erro.detail)
        })
    }

    // ---- helpers -----------------------------------------------------------

    const fracoesPorLote = (loteId: number) =>
        lotesFracionados?.filter((f) => f.data.loteOriginalId === loteId)

    const pesoFracionado = (loteId: number) =>
        fracoesPorLote(loteId)?.reduce((acc, f) => acc + (f.data.peso ?? 0), 0)

    // ---- render ------------------------------------------------------------

    return (
        <Page.Content>
            <Page.Header>
                <Page.Title>Lotes</Page.Title>

                <Page.Modal>
                    <ModalLoteFracionado
                        open={openFracionado}
                        setOpen={setOpenFracionado}
                        lotesBrutos={lotesBrutos}
                        pedidosVenda={pedidosVenda}
                        clientes={clientes}
                        isPending={isPending}
                        onSubmit={onCadastrarFracionado}
                        loteOriginalIdPreset={loteOriginalIdPreset}
                    />

                    <ModalLoteBruto
                        open={openBruto}
                        setOpen={setOpenBruto}
                        pedidosCompra={pedidosCompra}
                        fornecedores={fornecedores}
                        isPending={isPending}
                        onSubmit={onCadastrarBruto}
                    />
                </Page.Modal>
            </Page.Header>

            {error && (
                <div className="p-3 bg-red-100 rounded-md flex gap-2 items-center mb-2">
                    <button onClick={() => setError(null)} className="text-xs font-bold">X</button>
                    <p className="text-sm">{error}</p>
                </div>
            )}

            <Tabs defaultValue="brutos">
                <TabsList className="mb-4">
                    <TabsTrigger value="brutos" className="gap-2">
                        <Package size={15} />
                        Lotes Brutos
                        <span className="ml-1 text-xs bg-muted rounded-full px-2 py-0.5">
                            {lotesBrutos?.length}
                        </span>
                    </TabsTrigger>
                    <TabsTrigger value="fracionados" className="gap-2">
                        <PackageOpen size={15} />
                        Lotes Fracionados
                        <span className="ml-1 text-xs bg-muted rounded-full px-2 py-0.5">
                            {lotesFracionados?.length}
                        </span>
                    </TabsTrigger>
                </TabsList>

                {/* ── ABA: LOTES BRUTOS ───────────────────────────────── */}
                <TabsContent value="brutos">
                    <Page.Table>
                        {lotesBrutos?.length > 0 ? (
                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead>ID</TableHead>
                                        <TableHead>Nome</TableHead>
                                        <TableHead>Pedido Compra</TableHead>
                                        <TableHead>Peso Bruto</TableHead>
                                        <TableHead>Peso Fracionado</TableHead>
                                        <TableHead>Frações</TableHead>
                                        <TableHead></TableHead>
                                    </TableRow>
                                </TableHeader>

                                <TableBody>
                                    {lotesBrutos?.map((l) => {
                                        const fracoes = fracoesPorLote(l.metadata.id)
                                        const pesoBruto = l.data.peso ?? 0
                                        const pesoFrac = pesoFracionado(l.metadata.id)
                                        const pesoRestante = pesoBruto - pesoFrac

                                        const pedido = pedidosCompra.find(
                                            (p) => p.metadata.id === l.data.pedidoCompraId
                                        )
                                        const forn = fornecedores.find(
                                            (f) => pedido && f.metadata.id === pedido.data.fornecedorId
                                        )

                                        return (
                                            <TableRow key={l.metadata.id}>
                                                <TableCell>{l.metadata.id}</TableCell>

                                                <TableCell>
                                                    <div>
                                                        <p className="font-medium">{l.data.nome}</p>
                                                        {l.data.descricao && (
                                                            <p className="text-xs text-muted-foreground">
                                                                {l.data.descricao}
                                                            </p>
                                                        )}
                                                    </div>
                                                </TableCell>

                                                <TableCell>
                                                    <div className="text-sm">
                                                        <p>#{l.data.pedidoCompraId}</p>
                                                        {forn && (
                                                            <p className="text-xs text-muted-foreground">
                                                                {forn.data.nome}
                                                            </p>
                                                        )}
                                                    </div>
                                                </TableCell>

                                                <TableCell>
                                                    {pesoBruto > 0 ? `${pesoBruto} kg` : '—'}
                                                </TableCell>

                                                <TableCell>
                                                    <div className="text-sm">
                                                        <span>{pesoFrac > 0 ? `${pesoFrac} kg` : '—'}</span>
                                                        {pesoBruto > 0 && pesoFrac > 0 && (
                                                            <p className="text-xs text-muted-foreground">
                                                                {pesoRestante >= 0
                                                                    ? `${pesoRestante} kg restantes`
                                                                    : <span className="text-red-500">Excede em {Math.abs(pesoRestante)} kg</span>
                                                                }
                                                            </p>
                                                        )}
                                                    </div>
                                                </TableCell>

                                                <TableCell>
                                                    <div className="p-1 flex justify-center items-center rounded-xl border bg-muted w-fit px-3 text-sm">
                                                        {fracoes?.length}
                                                    </div>
                                                </TableCell>

                                                <TableCell>
                                                    <div className="flex gap-2">
                                                        <Button
                                                            size="sm"
                                                            className="bg-amber-500 hover:bg-amber-600 text-white"
                                                            title="Criar fração deste lote"
                                                            onClick={() => {
                                                                setLoteOriginalIdPreset(l.metadata.id)
                                                                setOpenFracionado(true)
                                                            }}
                                                        >
                                                            <Scissors size={15} />
                                                        </Button>

                                                        <Button
                                                            size="sm"
                                                            variant="destructive"
                                                            title="Excluir lote bruto"
                                                            disabled={fracoes?.length > 0}
                                                            onClick={() => onDeleteBruto(l.metadata.id)}
                                                        >
                                                            <Trash size={15} />
                                                        </Button>
                                                    </div>
                                                </TableCell>
                                            </TableRow>
                                        )
                                    })}
                                </TableBody>
                            </Table>
                        ) : (
                            <div className="flex justify-center items-center h-20 w-full">
                                <p className="text-muted-foreground text-sm">
                                    Nenhum lote bruto cadastrado
                                </p>
                            </div>
                        )}
                    </Page.Table>
                </TabsContent>

                {/* ── ABA: LOTES FRACIONADOS ──────────────────────────── */}
                <TabsContent value="fracionados">
                    <Page.Table>
                        {lotesFracionados?.length > 0 ? (
                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead>ID</TableHead>
                                        <TableHead>Nome</TableHead>
                                        <TableHead>Lote Bruto</TableHead>
                                        <TableHead>Pedido de Venda</TableHead>
                                        <TableHead>Peso</TableHead>
                                        <TableHead></TableHead>
                                    </TableRow>
                                </TableHeader>

                                <TableBody>
                                    {lotesFracionados?.map((f) => {
                                        const loteOriginal = lotesBrutos.find(
                                            (l) => l.metadata.id === f.data.loteOriginalId
                                        )
                                        const pedidoVenda = pedidosVenda.find(
                                            (p) => p.metadata.id === f.data.pedidoVendaId
                                        )
                                        const cliente = clientes.find(
                                            (c) => pedidoVenda && c.metadata.id === pedidoVenda.data.clienteId
                                        )

                                        return (
                                            <TableRow key={f.metadata.id}>
                                                <TableCell>{f.metadata.id}</TableCell>

                                                <TableCell>
                                                    <div>
                                                        <p className="font-medium">{f.data.nome}</p>
                                                        {f.data.descricao && (
                                                            <p className="text-xs text-muted-foreground">
                                                                {f.data.descricao}
                                                            </p>
                                                        )}
                                                    </div>
                                                </TableCell>

                                                <TableCell>
                                                    <div className="text-sm">
                                                        <p>#{f.data.loteOriginalId}</p>
                                                        {loteOriginal && (
                                                            <p className="text-xs text-muted-foreground">
                                                                {loteOriginal.data.nome}
                                                            </p>
                                                        )}
                                                    </div>
                                                </TableCell>

                                                <TableCell>
                                                    <div className="text-sm">
                                                        <p>#{f.data.pedidoVendaId}</p>
                                                        {cliente && (
                                                            <p className="text-xs text-muted-foreground">
                                                                {cliente.data.nome}
                                                            </p>
                                                        )}
                                                    </div>
                                                </TableCell>

                                                <TableCell>
                                                    {f.data.peso != null && f.data.peso > 0
                                                        ? `${f.data.peso} kg`
                                                        : '—'}
                                                </TableCell>

                                                <TableCell>
                                                    <Button
                                                        size="sm"
                                                        variant="destructive"
                                                        onClick={() => onDeleteFracionado(f.metadata.id)}
                                                    >
                                                        <Trash size={15} />
                                                    </Button>
                                                </TableCell>
                                            </TableRow>
                                        )
                                    })}
                                </TableBody>
                            </Table>
                        ) : (
                            <div className="flex justify-center items-center h-20 w-full">
                                <p className="text-muted-foreground text-sm">
                                    Nenhum lote fracionado cadastrado
                                </p>
                            </div>
                        )}
                    </Page.Table>
                </TabsContent>
            </Tabs>
        </Page.Content>
    )
}