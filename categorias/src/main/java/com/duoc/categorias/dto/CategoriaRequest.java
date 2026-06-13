package com.duoc.categorias.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para crear o actualizar una categoría")
public class CategoriaRequest {

    @Schema(description = "Nombre de la categoría", example = "Electronics")
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
}
