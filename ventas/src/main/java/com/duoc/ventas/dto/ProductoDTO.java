package com.duoc.ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos del producto obtenidos desde el microservicio de productos")
public class ProductoDTO {

    @Schema(description = "ID del producto", example = "1")
    private Integer id;

    @Schema(description = "Nombre del producto", example = "Teclado Gamer Razer")
    private String nombre;

    @Schema(description = "Cantidad disponible en stock", example = "50")
    private Integer cantidad;

    @Schema(description = "Precio del producto", example = "39990")
    private Integer precio;

    @Schema(description = "Categoría del producto", example = "Periféricos")
    private String categoria;
}
