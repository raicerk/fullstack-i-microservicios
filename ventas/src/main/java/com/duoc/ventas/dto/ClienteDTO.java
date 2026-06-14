package com.duoc.ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos del cliente obtenidos desde el microservicio de clientes")
public class ClienteDTO {

    @Schema(description = "ID del cliente", example = "1")
    private Long id;

    @Schema(description = "Nombre del cliente", example = "Juan Pérez")
    private String nombre;

    @Schema(description = "Email del cliente", example = "juan@example.com")
    private String email;
}
