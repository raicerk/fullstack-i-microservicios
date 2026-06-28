package com.duoc.ventas.service;

import com.duoc.ventas.client.ClienteClient;
import com.duoc.ventas.client.ProductoClient;
import com.duoc.ventas.dto.ClienteDTO;
import com.duoc.ventas.dto.ProductoDTO;
import com.duoc.ventas.dto.VentaDTO;
import com.duoc.ventas.dto.VentaRequest;
import com.duoc.ventas.exception.ClienteNotFoundException;
import com.duoc.ventas.exception.ProductoNotFoundException;
import com.duoc.ventas.exception.VentaNotFoundException;
import com.duoc.ventas.model.Venta;
import com.duoc.ventas.repository.VentaRepository;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - VentaService")
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private VentaService ventaService;

    private VentaRequest request;
    private Venta venta;
    private ClienteDTO clienteDTO;
    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        request = new VentaRequest();
        request.setClienteId(10L);
        request.setProductoId(5);
        request.setCantidad(2);
        request.setNotas("Despacho a domicilio");

        venta = new Venta(1L, 10L, 5, 2, 39990, 79980, LocalDateTime.of(2026, 6, 13, 20, 0), "Despacho a domicilio");

        clienteDTO = new ClienteDTO();
        clienteDTO.setId(10L);
        clienteDTO.setNombre("Juan Pérez");
        clienteDTO.setEmail("juan@example.com");

        productoDTO = new ProductoDTO();
        productoDTO.setId(5);
        productoDTO.setNombre("Teclado Gamer");
        productoDTO.setPrecio(39990);
        productoDTO.setCategoria("Periféricos");
    }

    @Test
    void shouldRegistrarVentaCorrectamente() {
        when(clienteClient.obtenerCliente(10L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProducto(5)).thenReturn(productoDTO);
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        VentaDTO resultado = ventaService.registrar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Pérez", resultado.getNombreCliente());
        assertEquals("Teclado Gamer", resultado.getNombreProducto());
        assertEquals(79980, resultado.getTotalVenta());
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    void shouldObtenerVentaPorId() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(clienteClient.obtenerCliente(10L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProducto(5)).thenReturn(productoDTO);

        VentaDTO resultado = ventaService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("Juan Pérez", resultado.getNombreCliente());
        assertEquals("Teclado Gamer", resultado.getNombreProducto());
    }

    @Test
    void shouldListarTodasLasVentas() {
        Venta otraVenta = new Venta(2L, 10L, 5, 1, 39990, 39990, LocalDateTime.of(2026, 6, 14, 10, 0), null);
        when(ventaRepository.findAll()).thenReturn(List.of(venta, otraVenta));
        when(clienteClient.obtenerCliente(10L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProducto(5)).thenReturn(productoDTO);

        List<VentaDTO> resultado = ventaService.listar();

        assertEquals(2, resultado.size());
        assertEquals(79980, resultado.get(0).getTotalVenta());
        assertEquals(39990, resultado.get(1).getTotalVenta());
    }

    @Test
    void shouldEliminarVentaCuandoExiste() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        ventaService.eliminar(1L);

        verify(ventaRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldLanzarClienteNotFoundCuandoClienteNoExiste() {
        when(clienteClient.obtenerCliente(10L)).thenThrow(feignNotFound("ClienteClient#obtenerCliente(Long)", "http://clientes/api/v1/clientes/10"));

        assertThrows(ClienteNotFoundException.class, () -> ventaService.registrar(request));
        verify(productoClient, never()).obtenerProducto(any());
        verify(ventaRepository, never()).save(any());
    }

    @Test
    void shouldLanzarProductoNotFoundCuandoProductoNoExiste() {
        when(clienteClient.obtenerCliente(10L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProducto(5)).thenThrow(feignNotFound("ProductoClient#obtenerProducto(Integer)", "http://productos/api/v1/productos/5"));

        assertThrows(ProductoNotFoundException.class, () -> ventaService.registrar(request));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    void shouldLanzarVentaNotFoundCuandoIdNoExiste() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(VentaNotFoundException.class, () -> ventaService.buscarPorId(99L));
    }

    private FeignException.NotFound feignNotFound(String methodKey, String url) {
        Request request = Request.create(Request.HttpMethod.GET, url, Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
        Response response = Response.builder()
                .status(404)
                .reason("Not Found")
                .request(request)
                .headers(Map.of())
                .build();
        return (FeignException.NotFound) FeignException.errorStatus(methodKey, response);
    }
}
