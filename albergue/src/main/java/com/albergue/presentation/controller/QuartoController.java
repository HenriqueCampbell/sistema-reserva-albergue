package com.albergue.presentation.controller;

import com.albergue.application.service.QuartoService;
import com.albergue.application.service.ReservaService;
import com.albergue.domain.model.Leito;
import com.albergue.domain.model.Quarto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/quartos")
public class QuartoController {

    private final QuartoService quartoService;
    private final ReservaService reservaService;

    public QuartoController(QuartoService quartoService, ReservaService reservaService) {
        this.quartoService = quartoService;
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<Quarto> cadastrar(@RequestBody Quarto quarto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quartoService.cadastrar(quarto));
    }

    @GetMapping
    public ResponseEntity<List<Quarto>> listarTodos() {
        return ResponseEntity.ok(quartoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quarto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(quartoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quarto> atualizar(@PathVariable Long id, @RequestBody Quarto quarto) {
        return ResponseEntity.ok(quartoService.atualizar(id, quarto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        quartoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<Leito>> consultarVagasDisponiveis(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        List<Leito> leitosDisponiveis = reservaService.consultarDisponibilidade(dataInicio, dataFim);
        return ResponseEntity.ok(leitosDisponiveis);
    }
}