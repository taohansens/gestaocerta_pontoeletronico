package com.taohansen.mod.pontoeletronico.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class PontoEletronico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;
    private LocalTime horaEntrada;
    private LocalTime horaSaida;
    private Double horasTrabalhadas;
    private Double horasExtras;

    private Long empregadoId;

    private boolean validadoPeloEmpregador;
}
