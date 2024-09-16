package com.taohansen.mod.pontoeletronico.services;

import com.taohansen.mod.pontoeletronico.dto.ValidacaoPontoDTO;
import com.taohansen.mod.pontoeletronico.dto.ValidacaoPontoInsertDTO;
import com.taohansen.mod.pontoeletronico.entities.PontoEletronico;
import com.taohansen.mod.pontoeletronico.entities.ValidacaoPonto;
import com.taohansen.mod.pontoeletronico.mapper.ValidacaoPontoMapper;
import com.taohansen.mod.pontoeletronico.repositories.PontoEletronicoRepository;
import com.taohansen.mod.pontoeletronico.repositories.ValidacaoPontoRepository;
import com.taohansen.mod.pontoeletronico.services.exceptions.ResourceNotFoundException;
import com.taohansen.mod.pontoeletronico.services.exceptions.ValidationDuplicatedException;
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
