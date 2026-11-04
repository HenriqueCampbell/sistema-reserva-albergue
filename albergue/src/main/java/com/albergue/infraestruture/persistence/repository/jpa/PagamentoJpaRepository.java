package com.albergue.infraestruture.persistence.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.albergue.domain.model.Pagamento;

public interface PagamentoJpaRepository extends JpaRepository<Pagamento, Long> {

}