package com.albergue.domain.repository;

import java.util.List;
import java.util.Optional;
import com.albergue.domain.model.Leito;

public interface LeitoRepository {
    //funcionalidades existentes("menu")
    Leito salvar(Leito leito);
    Optional<Leito> buscarPorId(Long id);
    List<Leito> listarTodos();
    void excluir(Long id);
}