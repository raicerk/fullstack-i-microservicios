package com.example.clientes.service;

import com.example.clientes.dto.ClienteDTO;
import com.example.clientes.dto.ClienteRequest;
import com.example.clientes.exception.ClienteNotFoundException;
import com.example.clientes.model.Cliente;
import com.example.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ClienteService.
 *
 * Se usa @ExtendWith(MockitoExtension.class) para que Mockito inyecte los mocks
 * automáticamente sin levantar el contexto de Spring.
 *
 * Estructura de cada prueba: Given - When - Then (AAA: Arrange, Act, Assert)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteGuardado;
    private ClienteRequest request;

    @BeforeEach
    void setUp() {
        clienteGuardado = new Cliente(1L, "Juan Pérez", "juan@example.com");

        request = new ClienteRequest();
        request.setNombre("Juan Pérez");
        request.setEmail("juan@example.com");
    }

    // =========================================================================
    // saveCliente()
    // =========================================================================

    @Test
    @DisplayName("saveCliente: debería guardar el cliente y retornar el DTO correctamente")
    void shouldGuardarClienteCorrectamente() {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);

        ClienteDTO resultado = clienteService.saveCliente(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("juan@example.com", resultado.getEmail());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    // =========================================================================
    // getClientes()
    // =========================================================================

    @Test
    @DisplayName("getClientes: debería retornar todos los clientes como lista de DTOs")
    void shouldListarTodosLosClientes() {
        Cliente otroCliente = new Cliente(2L, "María González", "maria@example.com");
        when(clienteRepository.findAll()).thenReturn(List.of(clienteGuardado, otroCliente));

        List<ClienteDTO> resultado = clienteService.getClientes();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        assertEquals("María González", resultado.get(1).getNombre());
    }

    @Test
    @DisplayName("getClientes: debería retornar lista vacía cuando no hay clientes")
    void shouldRetornarListaVaciaAlListar() {
        when(clienteRepository.findAll()).thenReturn(Collections.emptyList());

        List<ClienteDTO> resultado = clienteService.getClientes();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // getCliente()
    // =========================================================================

    @Test
    @DisplayName("getCliente: debería retornar el cliente correcto cuando el ID existe")
    void shouldBuscarClientePorIdCorrectamente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteGuardado));

        ClienteDTO resultado = clienteService.getCliente(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Pérez", resultado.getNombre());
    }

    @Test
    @DisplayName("getCliente: debería lanzar ClienteNotFoundException cuando el ID no existe")
    void shouldThrowClienteNotFoundAlBuscarPorId() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> clienteService.getCliente(99L));
    }

    // =========================================================================
    // actualizarCliente()
    // =========================================================================

    @Test
    @DisplayName("actualizarCliente: debería actualizar el cliente y retornar el DTO con los nuevos datos")
    void shouldActualizarClienteCorrectamente() {
        Cliente clienteActualizado = new Cliente(1L, "Juan Actualizado", "nuevo@example.com");
        request.setNombre("Juan Actualizado");
        request.setEmail("nuevo@example.com");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteGuardado));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteActualizado);

        ClienteDTO resultado = clienteService.actualizarCliente(1L, request);

        assertNotNull(resultado);
        assertEquals("Juan Actualizado", resultado.getNombre());
        assertEquals("nuevo@example.com", resultado.getEmail());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("actualizarCliente: debería lanzar ClienteNotFoundException cuando el ID no existe")
    void shouldThrowClienteNotFoundAlActualizar() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> clienteService.actualizarCliente(99L, request));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    // =========================================================================
    // eliminarCliente()
    // =========================================================================

    @Test
    @DisplayName("eliminarCliente: debería eliminar el cliente correctamente cuando el ID existe")
    void shouldEliminarClienteCorrectamente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteGuardado));
        doNothing().when(clienteRepository).deleteById(1L);

        clienteService.eliminarCliente(1L);

        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarCliente: debería lanzar ClienteNotFoundException cuando el ID no existe")
    void shouldThrowClienteNotFoundAlEliminar() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> clienteService.eliminarCliente(99L));
        verify(clienteRepository, never()).deleteById(any());
    }

    // =========================================================================
    // buscarPorNombre()
    // =========================================================================

    @Test
    @DisplayName("buscarPorNombre: debería retornar los clientes que coincidan con el nombre")
    void shouldBuscarClientesPorNombre() {
        when(clienteRepository.findClientesByNombreContainsIgnoreCase("juan"))
                .thenReturn(List.of(clienteGuardado));

        List<ClienteDTO> resultado = clienteService.buscarPorNombre("juan");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
    }
}
