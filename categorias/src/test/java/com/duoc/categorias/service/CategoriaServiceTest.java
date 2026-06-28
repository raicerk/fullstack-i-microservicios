package com.duoc.categorias.service;

import com.duoc.categorias.dto.CategoriaDTO;
import com.duoc.categorias.dto.CategoriaRequest;
import com.duoc.categorias.exception.CategoriaNotFoundException;
import com.duoc.categorias.model.Categoria;
import com.duoc.categorias.repository.CategoriaRepository;
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
 * Pruebas unitarias para CategoriaService.
 *
 * Se usa @ExtendWith(MockitoExtension.class) para que Mockito inyecte los mocks
 * automáticamente sin levantar el contexto de Spring.
 *
 * Estructura de cada prueba: Given - When - Then (AAA: Arrange, Act, Assert)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - CategoriaService")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoriaGuardada;
    private CategoriaRequest request;

    @BeforeEach
    void setUp() {
        categoriaGuardada = new Categoria(1, "Electronics");

        request = new CategoriaRequest();
        request.setNombre("Electronics");
    }

    // =========================================================================
    // guardar()
    // =========================================================================

    @Test
    @DisplayName("guardar: debería guardar la categoría y retornar el DTO correctamente")
    void shouldGuardarCategoriaCorrectamente() {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaGuardada);

        CategoriaDTO resultado = categoriaService.guardar(request);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Electronics", resultado.getNombre());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    // =========================================================================
    // listar()
    // =========================================================================

    @Test
    @DisplayName("listar: debería retornar todas las categorías como lista de DTOs")
    void shouldListarTodasLasCategorias() {
        Categoria otraCategoria = new Categoria(2, "Clothes");
        when(categoriaRepository.findAll()).thenReturn(List.of(categoriaGuardada, otraCategoria));

        List<CategoriaDTO> resultado = categoriaService.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Electronics", resultado.get(0).getNombre());
        assertEquals("Clothes", resultado.get(1).getNombre());
    }

    @Test
    @DisplayName("listar: debería retornar lista vacía cuando no hay categorías")
    void shouldRetornarListaVaciaAlListar() {
        when(categoriaRepository.findAll()).thenReturn(Collections.emptyList());

        List<CategoriaDTO> resultado = categoriaService.listar();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // buscarPorId()
    // =========================================================================

    @Test
    @DisplayName("buscarPorId: debería retornar la categoría correcta cuando el ID existe")
    void shouldBuscarCategoriaPorIdCorrectamente() {
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoriaGuardada));

        CategoriaDTO resultado = categoriaService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Electronics", resultado.getNombre());
    }

    @Test
    @DisplayName("buscarPorId: debería lanzar CategoriaNotFoundException cuando el ID no existe")
    void shouldThrowCategoriaNotFoundAlBuscarPorId() {
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFoundException.class, () -> categoriaService.buscarPorId(99));
    }

    // =========================================================================
    // actualizar()
    // =========================================================================

    @Test
    @DisplayName("actualizar: debería actualizar la categoría y retornar el DTO con los nuevos datos")
    void shouldActualizarCategoriaCorrectamente() {
        Categoria categoriaActualizada = new Categoria(1, "Electronics Pro");
        request.setNombre("Electronics Pro");

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoriaGuardada));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaActualizada);

        CategoriaDTO resultado = categoriaService.actualizar(1, request);

        assertNotNull(resultado);
        assertEquals("Electronics Pro", resultado.getNombre());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("actualizar: debería lanzar CategoriaNotFoundException cuando el ID no existe")
    void shouldThrowCategoriaNotFoundAlActualizar() {
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFoundException.class, () -> categoriaService.actualizar(99, request));
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    // =========================================================================
    // eliminar()
    // =========================================================================

    @Test
    @DisplayName("eliminar: debería eliminar la categoría correctamente cuando el ID existe")
    void shouldEliminarCategoriaCorrectamente() {
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoriaGuardada));
        doNothing().when(categoriaRepository).deleteById(1);

        categoriaService.eliminar(1);

        verify(categoriaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("eliminar: debería lanzar CategoriaNotFoundException cuando el ID no existe")
    void shouldThrowCategoriaNotFoundAlEliminar() {
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFoundException.class, () -> categoriaService.eliminar(99));
        verify(categoriaRepository, never()).deleteById(any());
    }

    // =========================================================================
    // buscarPorName()
    // =========================================================================

    @Test
    @DisplayName("buscarPorNombre: debería retornar las categorías que coincidan con el nombre")
    void shouldBuscarCategoriasPorName() {
        when(categoriaRepository.findCategoriasByNombreContainsIgnoreCase("elec"))
                .thenReturn(List.of(categoriaGuardada));

        List<CategoriaDTO> resultado = categoriaService.buscarPorNombre("elec");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Electronics", resultado.get(0).getNombre());
    }
}
