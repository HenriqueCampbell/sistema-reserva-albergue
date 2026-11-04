package com.albergue.application.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.albergue.domain.model.Quarto;
import com.albergue.domain.repository.QuartoRepository;

@Service
public class QuartoService {
    private final QuartoRepository quartoRepository;

    public QuartoService(QuartoRepository quartoRepository) {
        this.quartoRepository = quartoRepository;
    }

    public Quarto cadastrar(Quarto quarto) {
        return quartoRepository.salvar(quarto);
    }

    public Quarto buscarPorId(Long id) {
        return quartoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado com o ID: " + id));
    }

    public List<Quarto> listarTodos() {
        return quartoRepository.listarTodos();
    }

    public Quarto atualizar(Long id, Quarto quarto) {
        return quartoRepository.atualizar(id, quarto)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado com o ID: " + id));
    }

    public void excluir(Long id) {
        quartoRepository.excluir(id);
    }
}