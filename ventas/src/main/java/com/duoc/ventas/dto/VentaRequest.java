package com.duoc.ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para registrar una venta")
public class VentaRequest {

    @Schema(description = "ID del cliente que realiza la compra", example = "1")
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @Schema(description = "ID del producto a comprar", example = "1")
    @NotNull(message = "El ID del producto es obligatorio")
    private Integer productoId;

    @Schema(description = "Cantidad de unidades a comprar", example = "2")
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @Schema(description = "Observaciones opcionales", example = "Despacho a domicilio")
    private String notas;
}
