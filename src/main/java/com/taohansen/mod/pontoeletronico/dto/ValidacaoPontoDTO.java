package com.taohansen.mod.pontoeletronico.dto;

import lombok.Data;

@Data
public class ValidacaoPontoDTO {
    private String id;
    private String pontoEletronicoId;
    private String empregadoId;

    private String dataValidacao;
    private String dataTrabalhada;
    private String comentario;

    private String codigoSituacao;
    private String horasTrabalhadas;
    private String horasExtras;

}