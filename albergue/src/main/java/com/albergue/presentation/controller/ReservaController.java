package com.albergue.presentation.controller;

import com.albergue.application.service.ReservaService;
import com.albergue.domain.model.Reserva;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> criarReserva(@Valid @RequestBody CriarReservaRequest request) {
        Reserva reserva = reservaService.cadastrarReserva(
                request.clienteId(),
                request.dataEntrada(),
                request.dataSaida(),
                request.tipoReserva(),
                request.itensIds()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(reserva));
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodasReservas().stream()
                .map(ReservaController::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(reservaService.buscarReservaPorId(id)));
    }

    @PostMapping("/{id}/confirmar-pagamento-simulado")
    public ResponseEntity<ReservaResponse> confirmarPagamentoSimulado(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(reservaService.confirmarPagamentoSimulado(id)));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ReservaResponse>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.listarReservasPorCliente(clienteId).stream()
                .map(ReservaController::toResponse)
                .toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleInvalidReservationState(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleInvalidReservationRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new ApiError(exception.getMessage()));
    }

    private static ReservaResponse toResponse(Reserva reserva) {
        ClienteResumo cliente = reserva.getCliente() == null
                ? null
                : new ClienteResumo(reserva.getCliente().getId());
        List<LeitoResumo> leitos = reserva.getLeitos() == null
                ? List.of()
                : reserva.getLeitos().stream()
                        .map(leito -> new LeitoResumo(
                                leito.getId(),
                                leito.getDescricao(),
                                leito.getValor(),
                                leito.getQuarto() == null ? null : leito.getQuarto().getNumero()
                        ))
                        .toList();

        return new ReservaResponse(
                reserva.getId(),
                reserva.getStatus(),
                reserva.getTipoReserva(),
                reserva.getDataEntrada(),
                reserva.getDataSaida(),
                reserva.getValorTotal(),
                cliente,
                leitos
        );
    }

    public record ReservaResponse(
            Long id,
            Reserva.StatusReserva status,
            Reserva.TipoReserva tipoReserva,
            LocalDateTime dataEntrada,
            LocalDateTime dataSaida,
            BigDecimal valorTotal,
            ClienteResumo cliente,
            List<LeitoResumo> leitos
    ) {
    }

    public record ClienteResumo(Long id) {
    }

    public record LeitoResumo(Long id, String descricao, BigDecimal valor, Integer quartoNumero) {
    }

    public record ApiError(String message) {
    }

    public record CriarReservaRequest(
            @NotNull Long clienteId,
            @NotNull LocalDate dataEntrada,
            @NotNull LocalDate dataSaida,
            @NotNull Reserva.TipoReserva tipoReserva,
            @NotEmpty List<@NotNull Long> itensIds
    ) {
    }
}
