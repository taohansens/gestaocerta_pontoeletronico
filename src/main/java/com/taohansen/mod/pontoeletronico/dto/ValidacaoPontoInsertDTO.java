package com.taohansen.mod.pontoeletronico.dto;

import lombok.Data;

@Data
public class ValidacaoPontoInsertDTO {
    private Long pontoId;
    private String comentario;
    private String codigoSituacao;
}