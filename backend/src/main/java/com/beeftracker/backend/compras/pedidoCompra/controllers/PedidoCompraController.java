package com.beeftracker.backend.compras.pedidoCompra.controllers;

import com.beeftracker.backend.base.BaseController;
import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.base.exceptions.UnauthorizedException;
import com.beeftracker.backend.compras.pedidoCompra.form.AtualizarStatusForm;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBruto;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBrutoData;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompra;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompraData;
import com.beeftracker.backend.compras.pedidoCompra.service.PedidoCompraService;
import com.beeftracker.backend.usuarios.services.ValidateRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compras/pedido")
public class PedidoCompraController extends BaseController {

    private final PedidoCompraService service;
    private final ValidateRoleService roleValidator;

    public PedidoCompraController(PedidoCompraService service, ValidateRoleService roleValidator) {
        this.service = service;
        this.roleValidator = roleValidator;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestAttribute("userId") String userId, @RequestBody PedidoCompraData data) throws InvalidFormException, UnauthorizedException {
        roleValidator.validate(Long.parseLong(userId), List.of("ADMIN", "COMPRAS"));
        service.criar(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@RequestAttribute("userId") String userId, @PathVariable Long id, @RequestBody PedidoCompraData data)
            throws ResourceNotFoundException, InvalidFormException, UnauthorizedException {
        roleValidator.validate(Long.parseLong(userId), List.of("ADMIN", "COMPRAS"));
        service.editar(id, data);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@RequestAttribute("userId") String userId, @PathVariable Long id, @RequestBody AtualizarStatusForm request)
            throws ResourceNotFoundException, UnauthorizedException, InvalidFormException {
        roleValidator.validate(Long.parseLong(userId), List.of("ADMIN", "COMPRAS"));
        service.atualizarStatus(id, request.status());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoCompra> carregar(@RequestAttribute("userId") String userId, @PathVariable Long id) throws ResourceNotFoundException, UnauthorizedException {
        roleValidator.validate(Long.parseLong(userId), List.of("ADMIN", "COMPRAS"));

        return ResponseEntity.ok(service.carregar(id));
    }

    @GetMapping
    public ResponseEntity<List<PedidoCompra>> pesquisar(
            @RequestAttribute("userId") String userId,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page) throws UnauthorizedException {
        roleValidator.validate(Long.parseLong(userId), List.of("ADMIN", "COMPRAS"));
        return ResponseEntity.ok(service.pesquisar(fornecedorId, status, page));
    }

    @PostMapping("/{pedidoId}/lote")
    public ResponseEntity<?> criarLote(@RequestAttribute("userId") String userId, @PathVariable Long pedidoId, @RequestBody LoteBrutoData data)
            throws ResourceNotFoundException, InvalidFormException, UnauthorizedException {
        roleValidator.validate(Long.parseLong(userId), List.of("ADMIN", "COMPRAS"));
        service.criarLote(new LoteBrutoData(data.nome(), data.descricao(), data.peso(), pedidoId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{pedidoId}/lote/{id}")
    public ResponseEntity<?> editarLote(@RequestAttribute("userId") String userId,@PathVariable Long pedidoId, @PathVariable Long id,
            @RequestBody LoteBrutoData data) throws ResourceNotFoundException, InvalidFormException, UnauthorizedException {
        roleValidator.validate(Long.parseLong(userId), List.of("ADMIN", "COMPRAS"));
        service.editarLote(id, new LoteBrutoData(data.nome(), data.descricao(), data.peso(), pedidoId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pedidoId}/lote/{id}")
    public ResponseEntity<LoteBruto> carregarLote(@RequestAttribute("userId") String userId,@PathVariable Long pedidoId, @PathVariable Long id)
            throws ResourceNotFoundException, UnauthorizedException {
        roleValidator.validate(Long.parseLong(userId), List.of("ADMIN", "COMPRAS"));
        return ResponseEntity.ok(service.carregarLote(id));
    }

    @GetMapping("/{pedidoId}/lote")
    public ResponseEntity<List<LoteBruto>> pesquisarLotes(@RequestAttribute("userId") String userId,@PathVariable Long pedidoId)
            throws ResourceNotFoundException, UnauthorizedException {
        roleValidator.validate(Long.parseLong(userId), List.of("ADMIN", "COMPRAS"));
        return ResponseEntity.ok(service.pesquisarLotes(pedidoId));
    }

    @PatchMapping("/{pedidoId}/viagem/{viagemId}")
    public ResponseEntity<?> vincularViagem(@RequestAttribute("userId") String userId,@PathVariable Long pedidoId, @PathVariable Long viagemId)
            throws ResourceNotFoundException, UnauthorizedException {
        roleValidator.validate(Long.parseLong(userId), List.of("ADMIN", "COMPRAS"));
        service.vincularViagem(pedidoId, viagemId);
        return ResponseEntity.ok().build();
    }
}
