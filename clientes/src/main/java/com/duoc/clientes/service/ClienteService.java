package com.duoc.clientes.service;

import com.duoc.clientes.dto.ClienteDTO;
import com.duoc.clientes.dto.ClienteRequest;
import com.duoc.clientes.exception.ClienteNotFoundException;
import com.duoc.clientes.model.Cliente;
import com.duoc.clientes.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<ClienteDTO> getClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ClienteDTO getCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        return convertirADTO(cliente);
    }

    public ClienteDTO saveCliente(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setEmail(request.getEmail());
        log.info("Cliente almacenado correctamente: {}", cliente);
        return convertirADTO(clienteRepository.save(cliente));
    }

    public ClienteDTO actualizarCliente(Long id, ClienteRequest request) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        clienteExistente.setNombre(request.getNombre());
        clienteExistente.setEmail(request.getEmail());
        return convertirADTO(clienteRepository.save(clienteExistente));
    }

    public void eliminarCliente(Long id) {
        clienteRepository.findById(id).orElseThrow(() -> new ClienteNotFoundException(id));
        clienteRepository.deleteById(id);
    }

    public List<ClienteDTO> buscarPorNombre(String nombre) {
        return clienteRepository.findClientesByNombreContainsIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private ClienteDTO convertirADTO(Cliente cliente) {
        if (cliente == null) return null;
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setEmail(cliente.getEmail());
        return dto;
    }
}
