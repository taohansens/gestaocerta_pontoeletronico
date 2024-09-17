package com.gestaocerta.micropontoeletronico.repositories;

import com.gestaocerta.micropontoeletronico.entities.PontoEletronico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PontoEletronicoRepository extends JpaRepository<PontoEletronico, Long> {
    List<PontoEletronico> findByEmpregadoIdAndData(Long empregadoId, LocalDate data);

    Optional<PontoEletronico> findByEmpregadoIdAndDataAndHoraSaidaIsNull(Long empregadoId, LocalDate hoje);

    List<PontoEletronico> findByEmpregadoId(Long empregadoId);
}