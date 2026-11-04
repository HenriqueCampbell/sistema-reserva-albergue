package com.albergue.infraestruture.persistence.repository.Implementacao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.albergue.domain.model.Pagamento;
import com.albergue.domain.repository.PagamentoRepository;
import com.albergue.infraestruture.persistence.repository.jpa.PagamentoJpaRepository;

@Repository
public class PagamentoRepositoryImpl implements PagamentoRepository {
    private final PagamentoJpaRepository jpaRepository;

    public PagamentoRepositoryImpl(PagamentoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    //funcionalidades em si
    @Override
    public Pagamento salvar(Pagamento pagamento) {
        return jpaRepository.save(pagamento);
    }

    @Override
    public Optional<Pagamento> buscarPorId(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Pagamento> listarTodos() {
        return jpaRepository.findAll();
    }

    @Override
    public void ProcessarPagamento(Pagamento pagamento) {
        // Implementação do processamento de pagamento, por enquanto, melhor apenas salvar o pagamento no banco de dados
        jpaRepository.save(pagamento);
    }

    @Override
    public Optional<Pagamento> atualizar(Long id, Pagamento pagamento) {
        return jpaRepository.findById(id).map(existingPagamento -> {
            existingPagamento.setValor(pagamento.getValor());
            existingPagamento.setData(pagamento.getData());
            existingPagamento.setStatus(pagamento.getStatus());
            return jpaRepository.save(existingPagamento);
        });
    }
}