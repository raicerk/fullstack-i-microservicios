package com.duoc.ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Datos de la venta retornados por la API")
public class VentaDTO {

    @Schema(description = "ID único de la venta", example = "1")
    private Long id;

    @Schema(description = "ID del cliente que realizó la compra", example = "1")
    private Long clienteId;

    @Schema(description = "Nombre del cliente", example = "Juan Pérez")
    private String nombreCliente;

    @Schema(description = "ID del producto comprado", example = "1")
    private Integer productoId;

    @Schema(description = "Nombre del producto comprado", example = "Teclado Gamer Razer")
    private String nombreProducto;

    @Schema(description = "Cantidad de unidades compradas", example = "2")
    private Integer cantidad;

    @Schema(description = "Precio unitario al momento de la compra", example = "39990")
    private Integer precioUnitario;

    @Schema(description = "Valor total de la venta (cantidad * precioUnitario)", example = "79980")
    private Integer totalVenta;

    @Schema(description = "Fecha y hora exacta de la venta", example = "2026-06-13T20:00:00")
    private LocalDateTime fechaVenta;

    @Schema(description = "Observaciones opcionales de la venta", example = "Despacho a domicilio")
    private String notas;
}
