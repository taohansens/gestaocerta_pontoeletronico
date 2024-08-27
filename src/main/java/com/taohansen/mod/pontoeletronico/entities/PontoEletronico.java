package com.taohansen.mod.pontoeletronico.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class PontoEletronico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate data;
    private LocalTime horaEntrada;
    private LocalTime horaSaida;
    private Duration horasTrabalhadas;
    private Duration horasExtras;

    private Long empregadoId;
}
