package com.example.clientes.controller;

import com.example.clientes.dto.ClienteDTO;
import com.example.clientes.dto.ClienteRequest;
import com.example.clientes.exception.ClienteNotFoundException;
import com.example.clientes.service.ClienteService;
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
 * Pruebas unitarias para ClienteController.
 *
 * @WebMvcTest carga solo la capa web: el controlador, filtros y @RestControllerAdvice.
 * No levanta la base de datos ni el contexto completo de Spring.
 *
 * MockMvc permite simular peticiones HTTP reales y verificar el status code,
 * headers y cuerpo JSON de la respuesta.
 *
 * ClienteService se reemplaza por @MockitoBean — no ejecuta lógica real.
 */
@WebMvcTest(ClienteController.class)
@DisplayName("Pruebas unitarias - ClienteController")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ClienteDTO clienteDTO;
    private ClienteRequest requestValido;

    @BeforeEach
    void setUp() {
        clienteDTO = new ClienteDTO();
        clienteDTO.setId(1L);
        clienteDTO.setNombre("Juan Pérez");
        clienteDTO.setEmail("juan@example.com");

        requestValido = new ClienteRequest();
        requestValido.setNombre("Juan Pérez");
        requestValido.setEmail("juan@example.com");
    }

    // =========================================================================
    // POST /api/v1/clientes
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/clientes: debería crear el cliente y retornar 201")
    void shouldGuardarClienteYRetornar201() throws Exception {
        when(clienteService.saveCliente(any(ClienteRequest.class))).thenReturn(clienteDTO);

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/clientes: debería retornar 400 cuando los datos son inválidos")
    void shouldRetornar400CuandoDatosInvalidos() throws Exception {
        ClienteRequest requestInvalido = new ClienteRequest();
        requestInvalido.setNombre("");        // @NotBlank falla
        requestInvalido.setEmail("no-email"); // @Email falla

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // GET /api/v1/clientes
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/clientes: debería retornar la lista de clientes y status 200")
    void shouldListarClientesYRetornar200() throws Exception {
        when(clienteService.getClientes()).thenReturn(List.of(clienteDTO));

        mockMvc.perform(get("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Juan Pérez"));
    }

    @Test
    @DisplayName("GET /api/v1/clientes: debería retornar 204 cuando la lista está vacía")
    void shouldRetornar204CuandoListaVacia() throws Exception {
        when(clienteService.getClientes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // =========================================================================
    // GET /api/v1/clientes/{id}
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/clientes/{id}: debería retornar el cliente y status 200 cuando el ID existe")
    void shouldBuscarPorIdYRetornar200() throws Exception {
        when(clienteService.getCliente(1L)).thenReturn(clienteDTO);

        mockMvc.perform(get("/api/v1/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"));
    }

    @Test
    @DisplayName("GET /api/v1/clientes/{id}: debería retornar 404 cuando el ID no existe")
    void shouldRetornar404CuandoIdNoExiste() throws Exception {
        when(clienteService.getCliente(99L))
                .thenThrow(new ClienteNotFoundException(99L));

        mockMvc.perform(get("/api/v1/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Cliente no encontrado con id: 99"));
    }

    // =========================================================================
    // PUT /api/v1/clientes/{id}
    // =========================================================================

    @Test
    @DisplayName("PUT /api/v1/clientes/{id}: debería actualizar el cliente y retornar 200")
    void shouldActualizarClienteYRetornar200() throws Exception {
        when(clienteService.actualizarCliente(eq(1L), any(ClienteRequest.class))).thenReturn(clienteDTO);

        mockMvc.perform(put("/api/v1/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"));
    }

    // =========================================================================
    // DELETE /api/v1/clientes/{id}
    // =========================================================================

    @Test
    @DisplayName("DELETE /api/v1/clientes/{id}: debería eliminar el cliente y retornar 204")
    void shouldEliminarClienteYRetornar204() throws Exception {
        doNothing().when(clienteService).eliminarCliente(1L);

        mockMvc.perform(delete("/api/v1/clientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/clientes/{id}: debería retornar 404 cuando el ID no existe")
    void shouldRetornar404AlEliminarIdNoExistente() throws Exception {
        doThrow(new ClienteNotFoundException(99L)).when(clienteService).eliminarCliente(99L);

        mockMvc.perform(delete("/api/v1/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Cliente no encontrado con id: 99"));
    }
}
