package com.albergue.infraestruture.persistence.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.albergue.domain.model.Cliente;

public interface ClienteJpaRepository extends JpaRepository<Cliente, Long> {

}