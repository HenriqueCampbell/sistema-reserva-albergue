package com.albergue.infraestruture.persistence.repository.Implementacao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.albergue.domain.model.Cliente;
import com.albergue.domain.repository.ClienteRepository;
import com.albergue.infraestruture.persistence.repository.jpa.ClienteJpaRepository;

@Repository
public class ClienteRepositoryImpl implements ClienteRepository {
    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositoryImpl(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    //funcionalidades em si
    @Override
    public Cliente salvar(Cliente cliente) {
        return jpaRepository.save(cliente);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Cliente> listarTodos() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<Cliente> atualizar(Long id, Cliente cliente) {
       return jpaRepository.findById(id).map(existingCliente -> {
            existingCliente.setNome(cliente.getNome());
            existingCliente.setEmail(cliente.getEmail());
            existingCliente.setTelefone(cliente.getTelefone());
            existingCliente.setCpf(cliente.getCpf());
            if (cliente.getSenha() != null) {
                existingCliente.setSenha(cliente.getSenha());
            }
            return jpaRepository.save(existingCliente);
        });
    }

    @Override
    public void excluir(Long id) {
        jpaRepository.deleteById(id);
    }
}