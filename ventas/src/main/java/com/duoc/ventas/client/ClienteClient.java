package com.duoc.ventas.client;

import com.duoc.ventas.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clientes", url = "${clientes.api.url:http://localhost:8081/api/v1}")
public interface ClienteClient {

    @GetMapping("/clientes/{id}")
    ClienteDTO obtenerCliente(@PathVariable("id") Long id);
}
