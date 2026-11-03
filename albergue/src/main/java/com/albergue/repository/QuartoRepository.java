package com.albergue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.albergue.model.Quarto;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

}