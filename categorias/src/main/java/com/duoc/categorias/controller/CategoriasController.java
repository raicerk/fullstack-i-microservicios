package com.duoc.categorias.controller;

import com.duoc.categorias.dto.CategoriaDTO;
import com.duoc.categorias.dto.CategoriaRequest;
import com.duoc.categorias.service.CategoriaService;
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
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorías", description = "Operaciones relacionadas con la gestión de categorías")
public class CategoriasController {

    @Autowired
    private CategoriaService categoriaService;

    @Operation(summary = "Crear una categoría", description = "Crea una nueva categoría.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CategoriaDTO> guardar(@Valid @RequestBody CategoriaRequest request) {
        log.info("El request para crear una categoría fue: {}", request);
        return new ResponseEntity<>(categoriaService.guardar(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar categorías", description = "Retorna todas las categorías. Si se proporciona el parámetro 'name', filtra por nombre (búsqueda parcial, sin distinción de mayúsculas).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CategoriaDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No hay categorías que mostrar", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listar(
            @Parameter(description = "Nombre de la categoría para filtrar (opcional)") @RequestParam(required = false) String name) {
        List<CategoriaDTO> categorias;
        if (name != null) {
            categorias = categoriaService.buscarPorName(name);
        } else {
            categorias = categoriaService.listar();
        }
        if (categorias.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }

    @Operation(summary = "Buscar categoría por ID", description = "Retorna una categoría específica según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarPorId(
            @Parameter(description = "ID de la categoría a buscar", required = true) @PathVariable Integer id) {
        return new ResponseEntity<>(categoriaService.buscarPorId(id), HttpStatus.OK);
    }

    @Operation(summary = "Actualizar una categoría", description = "Actualiza el nombre de una categoría existente según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizar(
            @Parameter(description = "ID de la categoría a actualizar", required = true) @PathVariable Integer id,
            @Valid @RequestBody CategoriaRequest request) {
        return new ResponseEntity<>(categoriaService.actualizar(id, request), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar una categoría", description = "Elimina una categoría según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la categoría a eliminar", required = true) @PathVariable Integer id) {
        categoriaService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
