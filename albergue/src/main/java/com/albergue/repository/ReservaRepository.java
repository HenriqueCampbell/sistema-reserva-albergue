package com.albergue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.albergue.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

}