package com.taohansen.mod.pontoeletronico.repositories;

import com.taohansen.mod.pontoeletronico.entities.ValidacaoPonto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidacaoPontoRepository extends JpaRepository<ValidacaoPonto, Long> {
}