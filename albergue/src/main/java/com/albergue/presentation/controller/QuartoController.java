package com.albergue.presentation.controller;

import com.albergue.application.service.QuartoService;
import com.albergue.application.service.ReservaService;
import com.albergue.domain.model.Quarto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public ResponseEntity<List<LeitoDisponivelResponse>> consultarVagasDisponiveis(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        List<LeitoDisponivelResponse> leitosDisponiveis = reservaService.consultarDisponibilidade(dataInicio, dataFim)
                .stream()
                .map(leito -> new LeitoDisponivelResponse(
                        leito.getId(),
                        leito.getDescricao(),
                        leito.getValor(),
                        leito.getQuarto() == null ? null : new QuartoResumo(
                                leito.getQuarto().getId(),
                                leito.getQuarto().getNumero(),
                                leito.getQuarto().getCapacidade(),
                                leito.getQuarto().getDescricao(),
                                leito.getQuarto().isBanheiro()
                        )
                ))
                .toList();
        return ResponseEntity.ok(leitosDisponiveis);
    }

    public record LeitoDisponivelResponse(Long id, String descricao, BigDecimal valor, QuartoResumo quarto) {
    }

    public record QuartoResumo(Long id, Integer numero, Integer capacidade, String descricao, boolean banheiro) {
    }
}