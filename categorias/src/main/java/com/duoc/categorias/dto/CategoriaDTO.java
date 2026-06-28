package com.duoc.categorias.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos de la categoría retornados por la API")
public class CategoriaDTO {

    @Schema(description = "ID único de la categoría generado por la base de datos", example = "1")
    private Integer id;

    @Schema(description = "Nombre de la categoría", example = "Electronics")
    private String nombre;
}
