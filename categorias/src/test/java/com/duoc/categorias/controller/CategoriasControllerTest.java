package com.duoc.categorias.controller;

import com.duoc.categorias.dto.CategoriaDTO;
import com.duoc.categorias.dto.CategoriaRequest;
import com.duoc.categorias.exception.CategoriaNotFoundException;
import com.duoc.categorias.service.CategoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para CategoriasController.
 *
 * @WebMvcTest carga solo la capa web: el controlador, filtros y @RestControllerAdvice.
 * No levanta la base de datos ni el contexto completo de Spring.
 *
 * CategoriaService se reemplaza por @MockitoBean — no ejecuta lógica real.
 */
@WebMvcTest(CategoriasController.class)
@DisplayName("Pruebas unitarias - CategoriasController")
class CategoriasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CategoriaDTO categoriaDTO;
    private CategoriaRequest requestValido;

    @BeforeEach
    void setUp() {
        categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId(1);
        categoriaDTO.setNombre("Electronics");

        requestValido = new CategoriaRequest();
        requestValido.setNombre("Electronics");
    }

    // =========================================================================
    // POST /api/v1/categorias
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/categorias: debería crear la categoría y retornar 201")
    void shouldGuardarCategoriaYRetornar201() throws Exception {
        when(categoriaService.guardar(any(CategoriaRequest.class))).thenReturn(categoriaDTO);

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Electronics"));
    }

    @Test
    @DisplayName("POST /api/v1/categorias: debería retornar 400 cuando los datos son inválidos")
    void shouldRetornar400CuandoDatosInvalidos() throws Exception {
        CategoriaRequest requestInvalido = new CategoriaRequest();
        requestInvalido.setNombre(""); // @NotBlank falla

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // GET /api/v1/categorias
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/categorias: debería retornar la lista de categorías y status 200")
    void shouldListarCategoriasYRetornar200() throws Exception {
        when(categoriaService.listar()).thenReturn(List.of(categoriaDTO));

        mockMvc.perform(get("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Electronics"));
    }

    @Test
    @DisplayName("GET /api/v1/categorias: debería retornar 200 con lista vacía")
    void shouldRetornar200CuandoListaVacia() throws Exception {
        when(categoriaService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // GET /api/v1/categorias/{id}
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/categorias/{id}: debería retornar la categoría y status 200 cuando el ID existe")
    void shouldBuscarPorIdYRetornar200() throws Exception {
        when(categoriaService.buscarPorId(1)).thenReturn(categoriaDTO);

        mockMvc.perform(get("/api/v1/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Electronics"));
    }

    @Test
    @DisplayName("GET /api/v1/categorias/{id}: debería retornar 404 cuando el ID no existe")
    void shouldRetornar404CuandoIdNoExiste() throws Exception {
        when(categoriaService.buscarPorId(99))
                .thenThrow(new CategoriaNotFoundException(99));

        mockMvc.perform(get("/api/v1/categorias/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Categoría no encontrada con id: 99"));
    }

    // =========================================================================
    // PUT /api/v1/categorias/{id}
    // =========================================================================

    @Test
    @DisplayName("PUT /api/v1/categorias/{id}: debería actualizar la categoría y retornar 200")
    void shouldActualizarCategoriaYRetornar200() throws Exception {
        when(categoriaService.actualizar(eq(1), any(CategoriaRequest.class))).thenReturn(categoriaDTO);

        mockMvc.perform(put("/api/v1/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Electronics"));
    }

    // =========================================================================
    // DELETE /api/v1/categorias/{id}
    // =========================================================================

    @Test
    @DisplayName("DELETE /api/v1/categorias/{id}: debería eliminar la categoría y retornar 204")
    void shouldEliminarCategoriaYRetornar204() throws Exception {
        doNothing().when(categoriaService).eliminar(1);

        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/categorias/{id}: debería retornar 404 cuando el ID no existe")
    void shouldRetornar404AlEliminarIdNoExistente() throws Exception {
        doThrow(new CategoriaNotFoundException(99)).when(categoriaService).eliminar(99);

        mockMvc.perform(delete("/api/v1/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Categoría no encontrada con id: 99"));
    }
}
