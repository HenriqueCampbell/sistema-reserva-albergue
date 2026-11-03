package com.albergue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.albergue.model.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

}