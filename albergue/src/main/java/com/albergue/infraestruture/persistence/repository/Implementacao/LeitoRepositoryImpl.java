package com.albergue.infraestruture.persistence.repository.Implementacao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.albergue.domain.model.Leito;
import com.albergue.domain.repository.LeitoRepository;
import com.albergue.infraestruture.persistence.repository.jpa.LeitoJpaRepository;

@Repository
public class LeitoRepositoryImpl implements LeitoRepository {
    private final LeitoJpaRepository jpaRepository;

    public LeitoRepositoryImpl(LeitoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    //funcionalidades em si
    @Override
    public Leito salvar(Leito leito) {
        return jpaRepository.save(leito);
    }

    @Override
    public Optional<Leito> buscarPorId(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Leito> listarTodos() {
        return jpaRepository.findAll();
    }

    @Override
    public void excluir(Long id) {
        jpaRepository.deleteById(id);
    }
}