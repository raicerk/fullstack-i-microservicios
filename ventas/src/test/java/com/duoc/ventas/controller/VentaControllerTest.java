package com.duoc.ventas.controller;

import com.duoc.ventas.dto.VentaDTO;
import com.duoc.ventas.dto.VentaRequest;
import com.duoc.ventas.exception.VentaNotFoundException;
import com.duoc.ventas.service.VentaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VentaController.class)
@DisplayName("Pruebas unitarias - VentaController")
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VentaService ventaService;

    private VentaDTO ventaDTO;
    private VentaRequest requestValido;

    @BeforeEach
    void setUp() {
        ventaDTO = new VentaDTO();
        ventaDTO.setId(1L);
        ventaDTO.setClienteId(10L);
        ventaDTO.setNombreCliente("Juan Pérez");
        ventaDTO.setProductoId(5);
        ventaDTO.setNombreProducto("Teclado Gamer");
        ventaDTO.setCantidad(2);
        ventaDTO.setPrecioUnitario(39990);
        ventaDTO.setTotalVenta(79980);
        ventaDTO.setFechaVenta(LocalDateTime.of(2026, 6, 13, 20, 0));
        ventaDTO.setNotas("Despacho a domicilio");

        requestValido = new VentaRequest();
        requestValido.setClienteId(10L);
        requestValido.setProductoId(5);
        requestValido.setCantidad(2);
        requestValido.setNotas("Despacho a domicilio");
    }

    @Test
    void shouldListarVentasYRetornar200() throws Exception {
        when(ventaService.listar()).thenReturn(List.of(ventaDTO));

        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombreCliente").value("Juan Pérez"));
    }

    @Test
    void shouldBuscarVentaPorIdYRetornar200() throws Exception {
        when(ventaService.buscarPorId(1L)).thenReturn(ventaDTO);

        mockMvc.perform(get("/api/v1/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreProducto").value("Teclado Gamer"));
    }

    @Test
    void shouldRetornar404CuandoVentaNoExiste() throws Exception {
        when(ventaService.buscarPorId(99L)).thenThrow(new VentaNotFoundException(99L));

        mockMvc.perform(get("/api/v1/ventas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Venta no encontrada con id: 99"));
    }

    @Test
    void shouldRegistrarVentaYRetornar201() throws Exception {
        when(ventaService.registrar(any(VentaRequest.class))).thenReturn(ventaDTO);

        mockMvc.perform(post("/api/v1/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalVenta").value(79980));
    }

    @Test
    void shouldRetornar400CuandoRequestEsInvalido() throws Exception {
        VentaRequest requestInvalido = new VentaRequest();
        requestInvalido.setClienteId(10L);
        requestInvalido.setProductoId(5);
        requestInvalido.setCantidad(0);

        mockMvc.perform(post("/api/v1/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cantidad").value("La cantidad debe ser al menos 1"));
    }

    @Test
    void shouldEliminarVentaYRetornar204() throws Exception {
        doNothing().when(ventaService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/ventas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRetornar404AlEliminarVentaInexistente() throws Exception {
        doThrow(new VentaNotFoundException(77L)).when(ventaService).eliminar(77L);

        mockMvc.perform(delete("/api/v1/ventas/77"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Venta no encontrada con id: 77"));
    }
}
