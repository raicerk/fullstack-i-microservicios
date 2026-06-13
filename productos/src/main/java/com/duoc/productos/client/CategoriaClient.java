package com.duoc.productos.client;

import com.duoc.productos.dto.CategoriaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "categorias-service", url = "${categorias.api.url:http://localhost:8083/api/v1}")
public interface CategoriaClient {

    @GetMapping("/categorias")
    List<CategoriaDTO> obtenerCategorias();
}
