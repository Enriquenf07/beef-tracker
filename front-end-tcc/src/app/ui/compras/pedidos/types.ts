export interface PedidoCompra {
    data: {
        fornecedorId: number;
        valorTotal: number;
        status: 'RASCUNHO' | 'ABERTO' | 'FINALIZADO' | 'CANCELADO';
        observacao: string;
        dataEmissao: string;
        dataEntrega: string;
    };
    metadata: {
        id: number;
        token: string;
        criadoEm: string;
        atualizadoEm: string;
    };
}

export interface LoteBruto {
    data: {
        nome: string;
        descricao: string;
        peso: number;
        pedidoCompraId: number;
    };
    metadata: {
        id: number;
        token: string;
    };
}