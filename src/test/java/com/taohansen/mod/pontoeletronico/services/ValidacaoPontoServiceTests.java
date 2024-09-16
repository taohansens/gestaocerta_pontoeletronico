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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidacaoPontoServiceTests {

    @InjectMocks
    private ValidacaoPontoService service;

    @Mock
    private ValidacaoPontoRepository repository;
    @Mock
    private PontoEletronicoRepository pontoEletronicoRepository;

    @Mock
    private ValidacaoProducer validacaoProducer;

    @Mock
    private ValidacaoPontoMapper validacaoPontoMapper;

    private Long existingId;
    private Long existingPontoId;
    private Long nonExistingId;

    private ValidacaoPonto validacaoPonto;
    private ValidacaoPontoDTO validacaoPontoDTO;
    private PontoEletronico pontoEletronico1;
    private PontoEletronico pontoEletronico2;

    @BeforeEach
    void setUp() throws Exception {
        pontoEletronico1 = new PontoEletronico();
        pontoEletronico1.setEmpregadoId(1L);
        pontoEletronico1.setData(LocalDate.now());
        pontoEletronico1.setHoraEntrada(LocalDateTime.now().toLocalTime());
        pontoEletronico1.setHoraSaida(LocalDateTime.now().toLocalTime());
        pontoEletronico1.setHorasExtras(10.0);

        pontoEletronico2 = new PontoEletronico();
        pontoEletronico2.setEmpregadoId(1L);
        pontoEletronico2.setData(LocalDate.now());
        pontoEletronico2.setHoraEntrada(LocalDateTime.now().toLocalTime());
        pontoEletronico2.setHoraSaida(LocalDateTime.now().toLocalTime());
        pontoEletronico2.setHorasExtras(10.0);
        pontoEletronico2.setValidadoPeloEmpregador(true);

        validacaoPonto = new ValidacaoPonto();
        validacaoPonto.setId(1L);
        validacaoPonto.setPontoEletronicoId(1L);
        validacaoPonto.setEmpregadoId(1L);
        validacaoPonto.setDataValidacao(LocalDateTime.now());
        validacaoPonto.setComentario("OK");
        validacaoPonto.setCodigoSituacao("100");
        validacaoPonto.setHorasExtras(5.0);
        validacaoPonto.setHorasTrabalhadas(4.0);
        validacaoPonto.setDataTrabalhada(LocalDate.now().atStartOfDay());

        validacaoPontoDTO = new ValidacaoPontoDTO();
        validacaoPontoDTO.setId("1");
        validacaoPontoDTO.setPontoEletronicoId("1");
        validacaoPontoDTO.setEmpregadoId("1");
        validacaoPontoDTO.setDataValidacao(LocalDate.now().atStartOfDay().toString());
        validacaoPontoDTO.setComentario("OK");
        validacaoPontoDTO.setCodigoSituacao("100");
        validacaoPontoDTO.setHorasExtras("5.0");
        validacaoPontoDTO.setHorasTrabalhadas("4.0");
        validacaoPontoDTO.setDataTrabalhada(LocalDateTime.now().toString());
        validacaoPontoDTO.setEmpregadoId("1");
        validacaoPontoDTO.setPontoEletronicoId("1");

        existingId = validacaoPonto.getId();
        existingPontoId = 1L;
        nonExistingId = 1000L;
    }

    @Test
    public void validarByIdShouldInsertValidatePonto() {
        when(pontoEletronicoRepository.findById(existingPontoId)).thenReturn(Optional.of(pontoEletronico1));
        when(repository.save(any(ValidacaoPonto.class))).thenReturn(validacaoPonto);
        when(validacaoPontoMapper.toDTO(validacaoPonto)).thenReturn(validacaoPontoDTO);
        doNothing().when(validacaoProducer).sendValidacaoPonto(any(ValidacaoPonto.class));

        ValidacaoPontoInsertDTO validacaoPontoInsDTO = new ValidacaoPontoInsertDTO();
        validacaoPontoInsDTO.setPontoId(1L);
        validacaoPontoInsDTO.setComentario("OK");
        validacaoPontoInsDTO.setCodigoSituacao("100");
        ValidacaoPontoDTO insert = service.validarById(validacaoPontoInsDTO);

        assertThat(insert.getEmpregadoId()).isEqualTo(String.valueOf(validacaoPonto.getEmpregadoId()));
        assertThat(insert.getPontoEletronicoId()).isEqualTo(String.valueOf(validacaoPonto.getPontoEletronicoId()));
        assertThat(insert.getCodigoSituacao()).isEqualTo(String.valueOf(validacaoPonto.getCodigoSituacao()));

        verify(pontoEletronicoRepository, times(1)).findById(existingPontoId);
        verify(repository, times(1)).save(any(ValidacaoPonto.class));
    }

    @Test
    public void validarByIdShouldThrowResourceNotFoundExceptionWhenIdNotExists() {
        when(pontoEletronicoRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        ValidacaoPontoInsertDTO validacaoPontoInsDTO = new ValidacaoPontoInsertDTO();
        validacaoPontoInsDTO.setPontoId(nonExistingId);
        validacaoPontoInsDTO.setComentario("OK");
        validacaoPontoInsDTO.setCodigoSituacao("100");

        assertThrows(ResourceNotFoundException.class, () -> {
            service.validarById(validacaoPontoInsDTO);
        });

        verify(pontoEletronicoRepository, times(1)).findById(nonExistingId);
        verify(repository, never()).save(any(ValidacaoPonto.class));
    }

    @Test
    public void validarByIdShouldThrowDuplicatedExceptionWhenAlreadyValidated() {
        when(pontoEletronicoRepository.findById(existingId)).thenReturn(Optional.of(pontoEletronico2));

        ValidacaoPontoInsertDTO validacaoPontoInsDTO = new ValidacaoPontoInsertDTO();
        validacaoPontoInsDTO.setPontoId(existingId);
        validacaoPontoInsDTO.setComentario("OK");
        validacaoPontoInsDTO.setCodigoSituacao("100");

        assertThrows(ValidationDuplicatedException.class, () -> {
            service.validarById(validacaoPontoInsDTO);
        });

        verify(pontoEletronicoRepository, times(1)).findById(existingId);
        verify(repository, never()).save(any(ValidacaoPonto.class));
    }
}
