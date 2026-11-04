package com.albergue.application.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.albergue.domain.model.Pagamento;
import com.albergue.domain.repository.PagamentoRepository;

@Service
public class PagamentoService {
    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    public Pagamento cadastrarPagamento(Pagamento pagamento) {
        return pagamentoRepository.salvar(pagamento);
    }

    public Pagamento buscarPagamentoPorId(Long id) {
        return pagamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado com o ID: " + id));
    }

    public List<Pagamento> listarTodosPagamentos() {
        return pagamentoRepository.listarTodos();
    }

    public Pagamento atualizarPagamento(Long id, Pagamento pagamento) {
        return pagamentoRepository.atualizar(id, pagamento)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado com o ID: " + id));
    }

    public void processarPagamento(Pagamento pagamento) {
        pagamentoRepository.ProcessarPagamento(pagamento);
    }
}