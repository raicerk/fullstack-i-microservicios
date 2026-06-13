package com.duoc.categorias.exception;

public class CategoriaNotFoundException extends RuntimeException {
    public CategoriaNotFoundException(Integer id) {
        super("Categoría no encontrada con id: " + id);
    }
}
