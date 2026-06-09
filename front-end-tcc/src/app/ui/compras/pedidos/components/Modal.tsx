import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue
} from "@/components/ui/select"
import { Plus } from "lucide-react";
import { useState } from "react";

interface ModalProps {
    open: boolean;
    setOpen: React.Dispatch<React.SetStateAction<boolean>>;
    form: any;
    setForm: React.Dispatch<React.SetStateAction<any>>;
    fornecedorId: string;
    setFornecedorId: React.Dispatch<React.SetStateAction<string>>;
    fornecedores: any[];
    isPending: boolean;
    onHandleCadastro: (e: React.FormEvent<HTMLFormElement>) => void;
}


export function ModalPedido({
    open,
    setOpen,
    form,
    setForm,
    fornecedorId,
    setFornecedorId,
    fornecedores = [],
    isPending,
    onHandleCadastro
}: ModalProps) {
    return (
        <Dialog
            open={open}
            onOpenChange={() => {
                setForm({});
                setFornecedorId('');
                setOpen(prev => !prev);
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
                            value={fornecedorId}
                            onValueChange={(val) => setFornecedorId(val)}
                        >
                            <SelectTrigger>
                                <SelectValue placeholder="Selecione um fornecedor" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectGroup>
                                    {fornecedores.length === 0 ? (
                                        <SelectItem value="__none" disabled>
                                            Nenhum fornecedor cadastrado
                                        </SelectItem>
                                    ) : (
                                        fornecedores.map((f: any) => (
                                            <SelectItem
                                                key={f.metadata.id}
                                                value={String(f.metadata.id)}
                                            >
                                                {f.data.nome}
                                                {f.data.apelido ? ` (${f.data.apelido})` : ''}
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

                        <Button type="submit">Salvar</Button>
                    </form>
                )}
            </DialogContent>
        </Dialog>
    );
}



interface ModalLoteProps {
    open: boolean;
    setOpen: React.Dispatch<React.SetStateAction<boolean>>;
    isPending?: boolean;
    onHandleCadastro: (e: React.FormEvent<HTMLFormElement>) => void;
}

export function ModalLote({
    open,
    setOpen,
    isPending,
    onHandleCadastro
}: ModalLoteProps) {
    return (
        <Dialog
            open={open}
            onOpenChange={() => {
                setOpen(prev => !prev);
            }}
        >


            <DialogContent>
                <DialogHeader>
                    <DialogTitle>
                        {'Cadastrar Lote'}
                    </DialogTitle>
                </DialogHeader>

                {isPending ? (
                    <p>Carregando...</p>
                ) : (
                    <form onSubmit={onHandleCadastro} className="flex flex-col gap-2">
                        <input hidden name="id" />

                        <Input
                            placeholder="Nome do lote"
                            name="nome"
                            type="text"
                            required
                        />

                        <Input
                            placeholder="Peso (kg)"
                            name="peso"
                            type="number"
                            step="0.01"
                        />

                        <Button type="submit">Salvar</Button>
                    </form>
                )}
            </DialogContent>
        </Dialog>
    );
}







export function ModalViagem({ open, setOpen, viagens = [] }: any) {
    const [viagemId, setViagemId] = useState<string>("");
    console.log(viagens)
    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Selecionar Viagem</DialogTitle>
                </DialogHeader>

                <div className="py-4">
                    <Select
                        value={viagemId}
                        onValueChange={(val) => setViagemId(val)}
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
                </div>
            </DialogContent>
        </Dialog>
    );
}