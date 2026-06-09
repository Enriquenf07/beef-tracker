package com.beeftracker.backend.viagens.strategy;

import com.beeftracker.backend.compras.pedidoCompra.service.PedidoCompraService;
import com.beeftracker.backend.vendas.clientes.models.Cliente;
import com.beeftracker.backend.vendas.clientes.services.ClienteService;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVenda;
import com.beeftracker.backend.vendas.pedidoVendas.service.PedidoVendaService;
import com.beeftracker.backend.viagens.model.ViagemData;
import org.springframework.stereotype.Service;

import com.beeftracker.backend.viagens.model.StatusViagem;
import com.beeftracker.backend.viagens.model.Viagem;

import java.time.LocalDateTime;

@Service
public class Entregue extends AlterarStatus {

    private final PedidoCompraService compraService;
    private final PedidoVendaService vendaService;
    private final ClienteService clienteService;

    public Entregue(PedidoCompraService compraService, PedidoVendaService vendaService, ClienteService clienteService) {
        this.compraService = compraService;
        this.vendaService = vendaService;
        this.clienteService = clienteService;
    }

    @Override
    boolean validarStatus(StatusViagem atual) {
        return atual == StatusViagem.EM_TRANSITO || atual == StatusViagem.PENDENTE;
    }

    @Override
    Viagem sideEffect(Viagem viagem) {
        PedidoVenda venda = vendaService.findByViagem(viagem);
        try{
            if(venda != null){
                Cliente cliente = clienteService.findById(venda.data().clienteId());
                vendaService.enviarEmail(cliente.data().nome(), cliente.data().email(), viagem.metadata().token());
            }

        }catch (Exception e){

        }

        ViagemData dadosAtualizados = viagem.data()
                .withEntregueEm(LocalDateTime.now());
        return new Viagem(
                dadosAtualizados,
                viagem.metadata()
        );
    }

        @Override
    StatusViagem getStatus() {
       return StatusViagem.ENTREGUE   ;
    }

}
