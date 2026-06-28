package com.duoc.ventas.controller;

import com.duoc.ventas.dto.VentaDTO;
import com.duoc.ventas.dto.VentaRequest;
import com.duoc.ventas.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/ventas")
@Tag(name = "Ventas", description = "Operaciones relacionadas con la gestión de ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Operation(summary = "Registrar una venta", description = "Registra una nueva venta validando que el cliente y el producto existan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta registrada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VentaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<VentaDTO> registrar(@Valid @RequestBody VentaRequest request) {
        log.info("Registrando venta: {}", request);
        return new ResponseEntity<>(ventaService.registrar(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar ventas", description = "Retorna todas las ventas. Opcionalmente filtra por clienteId o productoId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ventas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VentaDTO.class)))),
            @ApiResponse(responseCode = "200", description = "Lista de ventas (puede estar vacía)")
    })
    @GetMapping
    public ResponseEntity<List<VentaDTO>> listar(
            @Parameter(description = "Filtrar por ID de cliente") @RequestParam(required = false) Long clienteId,
            @Parameter(description = "Filtrar por ID de producto") @RequestParam(required = false) Integer productoId) {

        List<VentaDTO> ventas;

        if (clienteId != null) {
            ventas = ventaService.buscarPorCliente(clienteId);
        } else if (productoId != null) {
            ventas = ventaService.buscarPorProducto(productoId);
        } else {
            ventas = ventaService.listar();
        }

        return new ResponseEntity<>(ventas, HttpStatus.OK);
    }

    @Operation(summary = "Buscar venta por ID", description = "Retorna una venta específica según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VentaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> buscarPorId(
            @Parameter(description = "ID de la venta", required = true) @PathVariable Long id) {
        return new ResponseEntity<>(ventaService.buscarPorId(id), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar una venta", description = "Elimina una venta según su ID. Solo para corrección de errores operativos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Venta eliminada exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la venta a eliminar", required = true) @PathVariable Long id) {
        ventaService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
