package com.taohansen.mod.pontoeletronico.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taohansen.mod.pontoeletronico.dto.PontoEletronicoMinDTO;
import com.taohansen.mod.pontoeletronico.dto.ValidacaoPontoDTO;
import com.taohansen.mod.pontoeletronico.dto.ValidacaoPontoInsertDTO;
import com.taohansen.mod.pontoeletronico.entities.PontoEletronico;
import com.taohansen.mod.pontoeletronico.services.PontoEletronicoService;
import com.taohansen.mod.pontoeletronico.services.ValidacaoPontoService;
import com.taohansen.mod.pontoeletronico.services.exceptions.PontoEletronicoException;
import com.taohansen.mod.pontoeletronico.services.exceptions.ResourceNotFoundException;
import com.taohansen.mod.pontoeletronico.services.exceptions.ValidationDuplicatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ValidacaoPontoController.class)
public class ValidacaoPontoControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ValidacaoPontoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private ValidacaoPontoDTO validacaoPontoDTO;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    public void validarByIdShouldReturnValidacaoPontoDTO() throws Exception {
        when(service.validarById(any(ValidacaoPontoInsertDTO.class)))
                .thenReturn(validacaoPontoDTO);

        ValidacaoPontoInsertDTO validacaoPontoInsDTO = new ValidacaoPontoInsertDTO();
        validacaoPontoInsDTO.setPontoId(1L);
        validacaoPontoInsDTO.setComentario("OK");
        validacaoPontoInsDTO.setCodigoSituacao("100");

        String jsonContent = objectMapper.writeValueAsString(validacaoPontoInsDTO);

        mockMvc.perform(post("/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pontoEletronicoId").value(validacaoPontoDTO.getPontoEletronicoId()));

        verify(service, times(1)).validarById(validacaoPontoInsDTO);
    }

    @Test
    public void validarByIdShouldThrowResourceNotFoundExceptionWhenPontoNotExists() throws Exception {
        when(service.validarById(any(ValidacaoPontoInsertDTO.class)))
                .thenThrow(new ResourceNotFoundException("Ponto not found"));

        ValidacaoPontoInsertDTO validacaoPontoInsDTO = new ValidacaoPontoInsertDTO();
        validacaoPontoInsDTO.setPontoId(1000L);
        validacaoPontoInsDTO.setComentario("OK");
        validacaoPontoInsDTO.setCodigoSituacao("100");

        String jsonContent = objectMapper.writeValueAsString(validacaoPontoInsDTO);

        mockMvc.perform(post("/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isNotFound());

        verify(service, times(1)).validarById(validacaoPontoInsDTO);
    }

    @Test
    public void validarByIdShouldThrowDuplicatedExceptionWhenPontoAlreadyValidated() throws Exception {
        when(service.validarById(any(ValidacaoPontoInsertDTO.class)))
                .thenThrow(new ValidationDuplicatedException("Ponto já validado"));

        ValidacaoPontoInsertDTO validacaoPontoInsDTO = new ValidacaoPontoInsertDTO();
        validacaoPontoInsDTO.setPontoId(1L);
        validacaoPontoInsDTO.setComentario("OK");
        validacaoPontoInsDTO.setCodigoSituacao("100");

        String jsonContent = objectMapper.writeValueAsString(validacaoPontoInsDTO);

        mockMvc.perform(post("/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).validarById(validacaoPontoInsDTO);
    }
}