package com.albergue.infraestruture.persistence.repository.Implementacao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.albergue.domain.model.Quarto;
import com.albergue.domain.repository.QuartoRepository;
import com.albergue.infraestruture.persistence.repository.jpa.QuartoJpaRepository;
import com.albergue.domain.model.Leito;

@Repository
public class QuartoRepositoryImpl implements QuartoRepository {
    private final QuartoJpaRepository jpaRepository;

    public QuartoRepositoryImpl(QuartoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    //funcionalidades em si
    @Override
    public Quarto salvar(Quarto quarto) {
        return jpaRepository.save(quarto);
    }

    @Override
    public Optional<Quarto> buscarPorId(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Quarto> listarTodos() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<Quarto> atualizar(Long id, Quarto quarto) {
       return jpaRepository.findById(id).map(existingQuarto -> {
            existingQuarto.setNumero(quarto.getNumero());
            existingQuarto.setDescricao(quarto.getDescricao());
            return jpaRepository.save(existingQuarto);
        });
    }

    @Override
    public void excluir(Long id) {
        jpaRepository.deleteById(id);
    }
}