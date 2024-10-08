package com.gestaocerta.micropontoeletronico.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PontoEletronicoMinDTO {
    private String id;
    private String data;
    private String horaEntrada;
    private String horaSaida;
    private String horasTrabalhadas;
    private String horasExtras;
}
