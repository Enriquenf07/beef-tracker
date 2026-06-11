package com.beeftracker.backend.vendas.pedidoVendas.controllers;

import com.beeftracker.backend.base.BaseController;
import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.base.exceptions.UnauthorizedException;
import com.beeftracker.backend.usuarios.services.ValidateRoleService;
import com.beeftracker.backend.vendas.pedidoVendas.form.AtualizarStatusForm;
import com.beeftracker.backend.vendas.pedidoVendas.models.LoteFracionado;
import com.beeftracker.backend.vendas.pedidoVendas.models.LoteFracionadoData;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVenda;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVendaData;
import com.beeftracker.backend.vendas.pedidoVendas.service.PedidoVendaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendas/pedido")
public class PedidoVendaController extends BaseController {

    private final PedidoVendaService service;
    private final ValidateRoleService roleService;

    public PedidoVendaController(PedidoVendaService service, ValidateRoleService roleService) {
        this.service = service;
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @RequestAttribute("userId") String userId,
            @RequestBody PedidoVendaData data)
            throws InvalidFormException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "VENDAS"));
        service.criar(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id,
            @RequestBody PedidoVendaData data)
            throws ResourceNotFoundException, InvalidFormException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "VENDAS"));
        service.editar(id, data);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id,
            @RequestBody AtualizarStatusForm request)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "VENDAS"));
        service.atualizarStatus(id, request.status());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoVenda> carregar(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "VENDAS"));
        return ResponseEntity.ok(service.carregar(id));
    }

    @GetMapping
    public ResponseEntity<List<PedidoVenda>> pesquisar(
            @RequestAttribute("userId") String userId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page)
            throws UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "VENDAS"));
        return ResponseEntity.ok(service.pesquisar(clienteId, status, page));
    }

    @PostMapping("/{pedidoId}/lote")
    public ResponseEntity<?> criarLote(
            @RequestAttribute("userId") String userId,
            @PathVariable Long pedidoId,
            @RequestBody LoteFracionadoData data)
            throws ResourceNotFoundException, InvalidFormException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "VENDAS"));
        service.criarLote(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{pedidoId}/lote/{id}")
    public ResponseEntity<?> editarLote(
            @RequestAttribute("userId") String userId,
            @PathVariable Long pedidoId,
            @PathVariable Long id,
            @RequestBody LoteFracionadoData data)
            throws ResourceNotFoundException, InvalidFormException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "VENDAS"));
        service.editarLote(id, data);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pedidoId}/lote/{id}")
    public ResponseEntity<LoteFracionado> carregarLote(
            @RequestAttribute("userId") String userId,
            @PathVariable Long pedidoId,
            @PathVariable Long id)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "VENDAS"));
        return ResponseEntity.ok(service.carregarLote(id));
    }

    @GetMapping("/{pedidoId}/lote")
    public ResponseEntity<List<LoteFracionado>> pesquisarLotes(
            @RequestAttribute("userId") String userId,
            @PathVariable Long pedidoId)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "VENDAS"));
        return ResponseEntity.ok(service.pesquisarLotes(pedidoId));
    }

    @PatchMapping("/{pedidoId}/vincular-viagem/{viagemId}")
    public ResponseEntity<?> vincularViagem(
            @RequestAttribute("userId") String userId,
            @PathVariable Long pedidoId,
            @PathVariable Long viagemId)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "VENDAS"));
        service.vincularViagem(pedidoId, viagemId);
        return ResponseEntity.ok().build();
    }
}