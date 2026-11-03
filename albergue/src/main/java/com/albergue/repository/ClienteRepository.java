package com.albergue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.albergue.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}