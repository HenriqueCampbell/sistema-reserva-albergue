package com.albergue.infraestruture.persistence.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


import com.albergue.domain.model.Reserva;

public interface ReservaJpaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteId(Long clienteId);
}