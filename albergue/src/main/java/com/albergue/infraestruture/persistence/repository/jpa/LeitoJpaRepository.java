package com.albergue.infraestruture.persistence.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.albergue.domain.model.Leito;

public interface LeitoJpaRepository extends JpaRepository<Leito, Long> {

}