package com.duoc.ventas.client;

import com.duoc.ventas.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "productos", url = "${productos.api.url:http://localhost:8082/api/v1}")
public interface ProductoClient {

    @GetMapping("/productos/{id}")
    ProductoDTO obtenerProducto(@PathVariable("id") Integer id);
}
