package com.gestaocerta.micropontoeletronico.services;

import com.gestaocerta.micropontoeletronico.entities.ValidacaoPonto;
import com.gestaocerta.micropontoeletronico.dto.ValidacaoPontoDTO;
import com.gestaocerta.micropontoeletronico.dto.ValidacaoPontoInsertDTO;
import com.gestaocerta.micropontoeletronico.entities.PontoEletronico;
import com.gestaocerta.micropontoeletronico.mapper.ValidacaoPontoMapper;
import com.gestaocerta.micropontoeletronico.repositories.PontoEletronicoRepository;
import com.gestaocerta.micropontoeletronico.repositories.ValidacaoPontoRepository;
import com.gestaocerta.micropontoeletronico.services.exceptions.ResourceNotFoundException;
import com.gestaocerta.micropontoeletronico.services.exceptions.ValidationDuplicatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValidacaoPontoService {
    private final ValidacaoPontoRepository validacaoPontoRepository;
    private final PontoEletronicoRepository pontoEletronicoRepository;
    private final ValidacaoPontoMapper validacaoPontoMapper;
    private final ValidacaoProducer validacaoProducer;


    public ValidacaoPontoDTO validarById(ValidacaoPontoInsertDTO validacaoDTO) {
        Optional<PontoEletronico> obj = pontoEletronicoRepository.findById(validacaoDTO.getPontoId());
        PontoEletronico entity = obj.orElseThrow(() -> new ResourceNotFoundException(String.format("Ponto %d não encontrado.", validacaoDTO.getPontoId())));

        if (entity.isValidadoPeloEmpregador()) {
            throw new ValidationDuplicatedException("Ponto já validado pelo empregador.");
        }

        ValidacaoPonto validacao = new ValidacaoPonto();
        validacao.setPontoEletronicoId(entity.getId());
        validacao.setEmpregadoId(entity.getEmpregadoId());
        validacao.setDataValidacao(LocalDateTime.now());
        validacao.setComentario(validacaoDTO.getComentario());
        validacao.setCodigoSituacao(validacaoDTO.getCodigoSituacao());
        validacao.setHorasExtras(entity.getHorasExtras());
        validacao.setHorasTrabalhadas(entity.getHorasTrabalhadas());
        validacao.setDataTrabalhada(entity.getData().atStartOfDay());
        validacao = validacaoPontoRepository.save(validacao);

        entity.setValidadoPeloEmpregador(true);
        pontoEletronicoRepository.save(entity);

        validacaoProducer.sendValidacaoPonto(validacao);
        return validacaoPontoMapper.toDTO(validacao);
    }

}
