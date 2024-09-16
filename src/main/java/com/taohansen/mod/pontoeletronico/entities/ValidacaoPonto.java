package com.taohansen.mod.pontoeletronico.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ValidacaoPonto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pontoEletronicoId;
    private Long empregadoId;

    private LocalDateTime dataValidacao;
    private LocalDateTime dataTrabalhada;
    private String comentario;

    private String codigoSituacao;

    private Double horasTrabalhadas;
    private Double horasExtras;

}
