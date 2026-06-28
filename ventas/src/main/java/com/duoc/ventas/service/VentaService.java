package com.duoc.ventas.service;

import com.duoc.ventas.client.ClienteClient;
import com.duoc.ventas.client.ProductoClient;
import com.duoc.ventas.dto.ClienteDTO;
import com.duoc.ventas.dto.ProductoDTO;
import com.duoc.ventas.dto.VentaDTO;
import com.duoc.ventas.dto.VentaRequest;
import com.duoc.ventas.exception.ClienteNotFoundException;
import com.duoc.ventas.exception.ProductoNotFoundException;
import com.duoc.ventas.exception.VentaNotFoundException;
import com.duoc.ventas.model.Venta;
import com.duoc.ventas.repository.VentaRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteClient clienteClient;

    @Autowired
    private ProductoClient productoClient;

    public VentaDTO registrar(VentaRequest request) {
        ClienteDTO cliente;
        try {
            cliente = clienteClient.obtenerCliente(request.getClienteId());
        } catch (FeignException.NotFound ex) {
            throw new ClienteNotFoundException(request.getClienteId());
        }
        log.info("Cliente validado: {}", cliente.getNombre());

        ProductoDTO producto;
        try {
            producto = productoClient.obtenerProducto(request.getProductoId());
        } catch (FeignException.NotFound ex) {
            throw new ProductoNotFoundException(request.getProductoId());
        }
        log.info("Producto validado: {}", producto.getNombre());

        // Calcular totales
        int total = producto.getPrecio() * request.getCantidad();

        Venta venta = new Venta();
        venta.setClienteId(request.getClienteId());
        venta.setProductoId(request.getProductoId());
        venta.setCantidad(request.getCantidad());
        venta.setPrecioUnitario(producto.getPrecio());
        venta.setTotalVenta(total);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setNotas(request.getNotas());

        Venta saved = ventaRepository.save(venta);
        log.info("Venta registrada correctamente: {}", saved);

        return convertirADTO(saved, cliente, producto);
    }

    public List<VentaDTO> listar() {
        return ventaRepository.findAll().stream()
                .map(this::enriquecerVenta)
                .collect(Collectors.toList());
    }

    public VentaDTO buscarPorId(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new VentaNotFoundException(id));
        return enriquecerVenta(venta);
    }

    public List<VentaDTO> buscarPorCliente(Long clienteId) {
        return ventaRepository.findByClienteId(clienteId).stream()
                .map(this::enriquecerVenta)
                .collect(Collectors.toList());
    }

    public List<VentaDTO> buscarPorProducto(Integer productoId) {
        return ventaRepository.findByProductoId(productoId).stream()
                .map(this::enriquecerVenta)
                .collect(Collectors.toList());
    }

    public void eliminar(Long id) {
        ventaRepository.findById(id).orElseThrow(() -> new VentaNotFoundException(id));
        ventaRepository.deleteById(id);
    }

    private VentaDTO enriquecerVenta(Venta venta) {
        ClienteDTO cliente = null;
        ProductoDTO producto = null;

        try {
            cliente = clienteClient.obtenerCliente(venta.getClienteId());
        } catch (FeignException e) {
            log.warn("No se pudo obtener cliente id={}: {}", venta.getClienteId(), e.getMessage());
        }

        try {
            producto = productoClient.obtenerProducto(venta.getProductoId());
        } catch (FeignException e) {
            log.warn("No se pudo obtener producto id={}: {}", venta.getProductoId(), e.getMessage());
        }

        return convertirADTO(venta, cliente, producto);
    }

    private VentaDTO convertirADTO(Venta venta, ClienteDTO cliente, ProductoDTO producto) {
        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setClienteId(venta.getClienteId());
        dto.setNombreCliente(cliente != null ? cliente.getNombre() : "Desconocido");
        dto.setProductoId(venta.getProductoId());
        dto.setNombreProducto(producto != null ? producto.getNombre() : "Desconocido");
        dto.setCantidad(venta.getCantidad());
        dto.setPrecioUnitario(venta.getPrecioUnitario());
        dto.setTotalVenta(venta.getTotalVenta());
        dto.setFechaVenta(venta.getFechaVenta());
        dto.setNotas(venta.getNotas());
        return dto;
    }
}
