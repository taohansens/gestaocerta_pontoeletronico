package com.taohansen.mod.pontoeletronico.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PontoEletronicoMinDTO {
    private String data;
    private String horaEntrada;
    private String horaSaida;
    private String horasTrabalhadas;
    private String horasExtras;
}
