package com.taohansen.mod.pontoeletronico.controllers;

import com.taohansen.mod.pontoeletronico.dto.PontoEletronicoMinDTO;
import com.taohansen.mod.pontoeletronico.entities.PontoEletronico;
import com.taohansen.mod.pontoeletronico.services.PontoEletronicoService;
import com.taohansen.mod.pontoeletronico.services.exceptions.PontoEletronicoException;
import com.taohansen.mod.pontoeletronico.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(PontoEletronicoController.class)
public class PontoEletronicoControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PontoEletronicoService service;

    private Long existingId;
    private Long nonExistingId;
    private Long empregadoId;
    private PontoEletronico pontoEletronico1;
    private PontoEletronicoMinDTO pontoEletronicoMinDTO;
    private LocalDate data;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;

        pontoEletronico1 = new PontoEletronico();
        pontoEletronico1.setEmpregadoId(1L);
        pontoEletronico1.setData(LocalDate.now());
        pontoEletronico1.setHoraEntrada(LocalDateTime.now().toLocalTime());
        pontoEletronico1.setHoraSaida(LocalDateTime.now().toLocalTime());
        pontoEletronico1.setHorasExtras(2.0);

        pontoEletronicoMinDTO = new PontoEletronicoMinDTO();
        pontoEletronicoMinDTO.setId("1");
        pontoEletronicoMinDTO.setHoraEntrada(LocalDateTime.now().toLocalTime().toString());
        pontoEletronicoMinDTO.setHoraSaida(LocalDateTime.now().toLocalTime().toString());
        pontoEletronicoMinDTO.setHorasExtras("2.0");

        empregadoId = pontoEletronico1.getEmpregadoId();
        data = LocalDate.of(2024, 9, 15);
    }

    @Test
    public void getAllPontosByEmpregadoIdShouldReturnListOfPontos() throws Exception {
        when(service.getAllByEmpregado(empregadoId)).thenReturn(Arrays.asList(pontoEletronicoMinDTO, pontoEletronicoMinDTO));

        mockMvc.perform(get("/ponto/{empregadoId}", empregadoId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(pontoEletronicoMinDTO.getId()))
                .andExpect(jsonPath("$[0].horasExtras").value(pontoEletronicoMinDTO.getHorasExtras()));

        verify(service, times(1)).getAllByEmpregado(existingId);
    }

    @Test
    public void getAllPontosByEmpregadoIdBySelectDateShouldReturnListOfPontos() throws Exception {
        when(service.getByDate(empregadoId, data)).thenReturn(Arrays.asList(pontoEletronicoMinDTO, pontoEletronicoMinDTO));

        mockMvc.perform(get("/ponto/{empregadoId}/consulta", empregadoId)
                        .param("data", data.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(pontoEletronicoMinDTO.getId()))
                .andExpect(jsonPath("$[0].horasExtras").value(pontoEletronicoMinDTO.getHorasExtras()));

        verify(service, times(1)).getByDate(empregadoId, data);
    }

    @Test
    public void getPontoByIdShouldReturnPontoById() throws Exception {
        when(service.getById(existingId)).thenReturn(pontoEletronicoMinDTO);

        mockMvc.perform(get("/ponto/consulta/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(pontoEletronicoMinDTO.getId()))
                .andExpect(jsonPath("$.horasExtras").value(pontoEletronicoMinDTO.getHorasExtras()));

        verify(service, times(1)).getById(existingId);
    }

    @Test
    public void getPontoByIdShouldThrowResourceNotFoundExceptionWhenIdNotExists() throws Exception {
        when(service.getById(nonExistingId)).thenThrow(new ResourceNotFoundException("Não encontrado"));

        mockMvc.perform(get("/ponto/consulta/{id}", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(service, times(1)).getById(nonExistingId);
    }

    @Test
    public void registrarEntradaShouldRegistrarEntrada() throws Exception {
        when(service.registrarEntrada(empregadoId)).thenReturn(pontoEletronicoMinDTO);

        mockMvc.perform(post("/ponto/{empregadoId}/entrada", empregadoId))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(pontoEletronicoMinDTO.getId()))
                .andExpect(jsonPath("$.horaEntrada").value(pontoEletronicoMinDTO.getHoraEntrada()));

        verify(service, times(1)).registrarEntrada(empregadoId);
    }

    @Test
    public void registrarEntradaShouldThrowResourceNotFoundExceptionWhenEmpregadoIdNotExists() throws Exception {
        when(service.registrarEntrada(nonExistingId)).thenThrow(new ResourceNotFoundException("Não Encontrado"));

        mockMvc.perform(post("/ponto/{empregadoId}/entrada", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(service, times(1)).registrarEntrada(nonExistingId);
    }

    @Test
    public void registrarEntradaShouldThrowPontoEletronicoExceptionWhenPontoIsOpen() throws Exception {
        when(service.registrarEntrada(existingId)).thenThrow(new PontoEletronicoException("Outro Ponto Aberto"));

        mockMvc.perform(post("/ponto/{empregadoId}/entrada", existingId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(service, times(1)).registrarEntrada(existingId);
    }

    @Test
    public void registrarSaidaShouldRegistrarSaida() throws Exception {
        when(service.registrarSaida(empregadoId)).thenReturn(pontoEletronicoMinDTO);

        mockMvc.perform(post("/ponto/{empregadoId}/saida", empregadoId))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(pontoEletronicoMinDTO.getId()))
                .andExpect(jsonPath("$.horaSaida").value(pontoEletronicoMinDTO.getHoraSaida()));

        verify(service, times(1)).registrarSaida(empregadoId);
    }

    @Test
    public void registrarSaidaShouldThrowResourceNotFoundExceptionWhenEmpregadoIdNotExists() throws Exception {
        when(service.registrarSaida(nonExistingId)).thenThrow(new ResourceNotFoundException("Não Encontrado"));

        mockMvc.perform(post("/ponto/{empregadoId}/saida", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(service, times(1)).registrarSaida(nonExistingId);
    }

    @Test
    public void registrarSaidaShouldThrowPontoEletronicoExceptionWhenPontoIsClosed() throws Exception {
        when(service.registrarSaida(existingId)).thenThrow(new PontoEletronicoException("Ponto Fechado"));

        mockMvc.perform(post("/ponto/{empregadoId}/saida", existingId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(service, times(1)).registrarSaida(existingId);
    }


}