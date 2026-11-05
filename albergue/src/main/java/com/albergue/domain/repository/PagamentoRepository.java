package com.albergue.domain.repository;

import java.util.List;
import java.util.Optional;
import com.albergue.domain.model.Pagamento;

public interface PagamentoRepository {
    //funcionalidades existentes("menu")
    Pagamento salvar(Pagamento pagamento);
    Optional<Pagamento> buscarPorId(Long id);
    List<Pagamento> listarTodos();
    void ProcessarPagamento(Pagamento pagamento);
    Optional<Pagamento> atualizar(Long id, Pagamento pagamento);
}