package com.example.clientes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestión de Clientes")
                        .description("API REST para la gestión de clientes. Permite crear, listar, buscar, actualizar y eliminar clientes.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("DUOC UC - Fullstack I")
                                .url("https://www.duoc.cl")));
    }
}
