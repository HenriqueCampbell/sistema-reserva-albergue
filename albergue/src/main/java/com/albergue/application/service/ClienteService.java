package com.albergue.application.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.albergue.domain.model.Cliente;
import com.albergue.domain.repository.ClienteRepository;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente cadastrar(Cliente cliente) {
        return clienteRepository.salvar(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    public Cliente atualizar(Long id, Cliente cliente) {
        return clienteRepository.atualizar(id, cliente)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
    }

    public void excluir(Long id) {
        clienteRepository.excluir(id);
    }
}