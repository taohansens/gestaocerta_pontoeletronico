package com.gestaocerta.micropontoeletronico.repositories;

import com.gestaocerta.micropontoeletronico.entities.ValidacaoPonto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidacaoPontoRepository extends JpaRepository<ValidacaoPonto, Long> {
}