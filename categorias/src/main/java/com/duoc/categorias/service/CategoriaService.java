package com.duoc.categorias.service;

import com.duoc.categorias.dto.CategoriaDTO;
import com.duoc.categorias.dto.CategoriaRequest;
import com.duoc.categorias.exception.CategoriaNotFoundException;
import com.duoc.categorias.model.Categoria;
import com.duoc.categorias.repository.CategoriaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> listar() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO buscarPorId(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
        return convertirADTO(categoria);
    }

    public List<CategoriaDTO> buscarPorNombre(String nombre) {
        return categoriaRepository.findCategoriasByNombreContainsIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO guardar(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        log.info("Categoría almacenada correctamente: {}", categoria);
        return convertirADTO(categoriaRepository.save(categoria));
    }

    public CategoriaDTO actualizar(Integer id, CategoriaRequest request) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
        categoriaExistente.setNombre(request.getNombre());
        return convertirADTO(categoriaRepository.save(categoriaExistente));
    }

    public void eliminar(Integer id) {
        categoriaRepository.findById(id).orElseThrow(() -> new CategoriaNotFoundException(id));
        categoriaRepository.deleteById(id);
    }

    private CategoriaDTO convertirADTO(Categoria categoria) {
        if (categoria == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        return dto;
    }
}
