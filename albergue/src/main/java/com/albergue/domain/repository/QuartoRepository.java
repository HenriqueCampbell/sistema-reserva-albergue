package com.albergue.domain.repository;

import java.util.List;
import java.util.Optional;
import com.albergue.domain.model.Quarto;

public interface QuartoRepository {
    //funcionalidades existentes("menu")
    Quarto salvar(Quarto quarto);
    Optional<Quarto> buscarPorId(Long id);
    List<Quarto> listarTodos();
    Optional<Quarto> atualizar(Long id, Quarto quarto);
    void excluir(Long id);
}