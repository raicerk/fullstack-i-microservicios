package com.duoc.clientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para crear o actualizar un cliente")
public class ClienteRequest {

    @Schema(description = "Nombre del cliente", example = "Juan Pérez")
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Schema(description = "Correo electrónico del cliente", example = "juan@example.com")
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe tener un formato válido")
    private String email;
}
