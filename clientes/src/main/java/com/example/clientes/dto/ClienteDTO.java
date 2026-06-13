package com.example.clientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos del cliente retornados por la API")
public class ClienteDTO {

    @Schema(description = "ID único del cliente generado por la base de datos", example = "1")
    private Long id;

    @Schema(description = "Nombre del cliente", example = "Juan Pérez")
    private String nombre;

    @Schema(description = "Correo electrónico del cliente", example = "juan@example.com")
    private String email;
}
