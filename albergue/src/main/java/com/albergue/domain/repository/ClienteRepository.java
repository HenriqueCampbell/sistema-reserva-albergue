package com.albergue.domain.repository;

import java.util.List;
import java.util.Optional;
import com.albergue.domain.model.Cliente;
import com.albergue.domain.model.Quarto;

public interface ClienteRepository {
    //funcionalidades existentes("menu")
    Cliente salvar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long id);
    List<Cliente> listarTodos();
    public Optional<Cliente> atualizar(Long id, Cliente cliente);
    void excluir(Long id);
}