package com.gestaocerta.micropontoeletronico.mapper;

import com.gestaocerta.micropontoeletronico.dto.PontoEletronicoMinDTO;
import com.gestaocerta.micropontoeletronico.entities.PontoEletronico;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class PontoEletronicoMapper {

    public PontoEletronicoMinDTO toMinDto(PontoEletronico pontoEletronico) {
        if (pontoEletronico == null) {
            return null;
        }
        PontoEletronicoMinDTO dto = new PontoEletronicoMinDTO();
        dto.setId(String.valueOf(pontoEletronico.getId()));
        dto.setData(pontoEletronico.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dto.setHoraEntrada(pontoEletronico.getHoraEntrada() != null
                ? pontoEletronico.getHoraEntrada().format(DateTimeFormatter.ofPattern("HH:mm"))
                : "-"
        );

        dto.setHoraSaida(pontoEletronico.getHoraSaida() != null
                ? pontoEletronico.getHoraSaida().format(DateTimeFormatter.ofPattern("HH:mm"))
                : "-"
        );
        dto.setHorasTrabalhadas(pontoEletronico.getHorasTrabalhadas() != null
                ? String.valueOf(pontoEletronico.getHorasTrabalhadas()) : "-");

        dto.setHorasExtras(pontoEletronico.getHorasExtras() != null
                ? String.valueOf(pontoEletronico.getHorasExtras()): "-");
        return dto;
    }
}
