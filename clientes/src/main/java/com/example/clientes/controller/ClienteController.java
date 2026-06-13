package com.example.clientes.controller;

import com.example.clientes.dto.ClienteDTO;
import com.example.clientes.dto.ClienteRequest;
import com.example.clientes.service.ClienteService;
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
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Operaciones relacionadas con la gestión de clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "Listar clientes", description = "Retorna todos los clientes. Si se proporciona el parámetro 'nombre', filtra por nombre (búsqueda parcial, sin distinción de mayúsculas).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ClienteDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No hay clientes que mostrar", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> obtenerClientes(
            @Parameter(description = "Nombre del cliente para filtrar (opcional)") @RequestParam(required = false) String nombre) {
        List<ClienteDTO> clientes;
        if (nombre != null) {
            clientes = clienteService.buscarPorNombre(nombre);
        } else {
            clientes = clienteService.getClientes();
        }
        if (clientes.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @Operation(summary = "Buscar cliente por ID", description = "Retorna un cliente específico según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerCliente(
            @Parameter(description = "ID del cliente a buscar", required = true) @PathVariable Long id) {
        return new ResponseEntity<>(clienteService.getCliente(id), HttpStatus.OK);
    }

    @Operation(summary = "Crear un cliente", description = "Crea un nuevo cliente con nombre y email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ClienteDTO> guardarCliente(@Valid @RequestBody ClienteRequest request) {
        log.info("El request para crear un cliente fue: {}", request);
        return new ResponseEntity<>(clienteService.saveCliente(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un cliente", description = "Actualiza todos los campos de un cliente existente según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente(
            @Parameter(description = "ID del cliente a actualizar", required = true) @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        return new ResponseEntity<>(clienteService.actualizarCliente(id, request), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un cliente", description = "Elimina un cliente según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(
            @Parameter(description = "ID del cliente a eliminar", required = true) @PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
