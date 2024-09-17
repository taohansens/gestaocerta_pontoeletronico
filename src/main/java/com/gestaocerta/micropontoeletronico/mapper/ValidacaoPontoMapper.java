package com.gestaocerta.micropontoeletronico.mapper;

import com.gestaocerta.micropontoeletronico.entities.ValidacaoPonto;
import com.gestaocerta.micropontoeletronico.dto.ValidacaoPontoDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ValidacaoPontoMapper {

    public ValidacaoPontoDTO toDTO(ValidacaoPonto validacao) {
        if (validacao == null) {
            return null;
        }
        ValidacaoPontoDTO dto = new ValidacaoPontoDTO();
        dto.setId(String.valueOf(validacao.getId()));
        dto.setPontoEletronicoId(String.valueOf(validacao.getPontoEletronicoId()));
        dto.setEmpregadoId(String.valueOf(validacao.getEmpregadoId()));
        dto.setCodigoSituacao(String.valueOf(validacao.getCodigoSituacao()));
        dto.setComentario(validacao.getComentario());
        dto.setDataValidacao(validacao.getDataValidacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dto.setHorasExtras(String.valueOf(validacao.getHorasExtras()));
        dto.setHorasTrabalhadas(String.valueOf(validacao.getHorasTrabalhadas()));
        dto.setDataTrabalhada(validacao.getDataTrabalhada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return dto;
    }
}
