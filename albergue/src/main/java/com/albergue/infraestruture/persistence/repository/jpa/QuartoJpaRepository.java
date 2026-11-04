package com.albergue.infraestruture.persistence.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.albergue.domain.model.Quarto;

public interface QuartoJpaRepository extends JpaRepository<Quarto, Long> {

}