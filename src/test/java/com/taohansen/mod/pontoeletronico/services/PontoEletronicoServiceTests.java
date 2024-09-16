package com.taohansen.mod.pontoeletronico.services;

import com.taohansen.mod.pontoeletronico.config.EmpregadoClient;
import com.taohansen.mod.pontoeletronico.config.EmpregadoDTO;
import com.taohansen.mod.pontoeletronico.dto.PontoEletronicoMinDTO;
import com.taohansen.mod.pontoeletronico.entities.PontoEletronico;
import com.taohansen.mod.pontoeletronico.mapper.PontoEletronicoMapper;
import com.taohansen.mod.pontoeletronico.repositories.PontoEletronicoRepository;
import com.taohansen.mod.pontoeletronico.services.exceptions.PontoEletronicoException;
import com.taohansen.mod.pontoeletronico.services.exceptions.ResourceNotFoundException;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PontoEletronicoServiceTests {
    @InjectMocks
    private PontoEletronicoService service;

    @Mock
    private PontoEletronicoRepository repository;

    @Mock
    private PontoEletronicoMapper pontoEletronicoMapper;

    @Mock
    private EmpregadoClient empregadoClient;

    private Long existingId;
    private Long nonExistingId;
    private Long empregadoId;
    private PontoEletronico pontoEletronico1;
    private PontoEletronico pontoEletronico2;
    private PontoEletronico pontoEletronico3;
    private PontoEletronicoMinDTO pontoEletronicoDTO1;
    private PontoEletronicoMinDTO pontoEletronicoDTO3;
    EmpregadoDTO empregadoDTO = new EmpregadoDTO();

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        empregadoId = 1L;

        pontoEletronico1 = new PontoEletronico();
        pontoEletronico1.setEmpregadoId(1L);
        pontoEletronico1.setData(LocalDate.now());
        pontoEletronico1.setHoraEntrada(LocalDateTime.now().toLocalTime());
        pontoEletronico1.setHoraSaida(LocalDateTime.now().toLocalTime());
        pontoEletronico1.setHorasExtras(10.0);

        pontoEletronico2 = new PontoEletronico();
        pontoEletronico2.setEmpregadoId(2L);
        pontoEletronico2.setData(LocalDate.now());
        pontoEletronico2.setHoraEntrada(LocalDateTime.now().toLocalTime().minusHours(10));
        pontoEletronico2.setHoraSaida(LocalDateTime.now().toLocalTime().minusHours(5));
        pontoEletronico2.setHorasExtras(2.0);

        pontoEletronico3 = new PontoEletronico();
        pontoEletronico3.setEmpregadoId(1L);
        pontoEletronico3.setData(LocalDate.now().minusDays(2));
        pontoEletronico3.setHoraEntrada(LocalDateTime.now().toLocalTime().minusHours(10));
        pontoEletronico3.setHoraSaida(LocalDateTime.now().toLocalTime().minusHours(5));
        pontoEletronico3.setHorasExtras(8.0);

        pontoEletronicoDTO1 = new PontoEletronicoMinDTO();
        pontoEletronicoDTO1.setId("1");
        pontoEletronicoDTO1.setHorasExtras("10.0");

        pontoEletronicoDTO3 = new PontoEletronicoMinDTO();
        pontoEletronicoDTO3.setId("1");
        pontoEletronicoDTO3.setHorasExtras("8.0");

        empregadoDTO.setId(empregadoId);
        empregadoDTO.setNome("Empregado Teste");
        empregadoDTO.setJornadaDiaria("8");
    }

    @Test
    public void getByIdShouldReturnPontoEletronicoMinDTOWhenIdExists() {
        when(repository.findById(existingId)).thenReturn(Optional.of(pontoEletronico1));
        when(pontoEletronicoMapper.toMinDto(pontoEletronico1)).thenReturn(pontoEletronicoDTO1);

        PontoEletronicoMinDTO result = service.getById(existingId);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingId.toString());
        assertThat(result.getHorasExtras()).isEqualTo("10.0");
    }

    @Test
    public void getByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(nonExistingId));

        verify(repository, times(1)).findById(nonExistingId);
        verify(pontoEletronicoMapper, never()).toMinDto(any());
    }

    @Test
    public void getAllByEmpregadoShouldReturnListOfPontosByEmpregadoId() {
        when(repository.findByEmpregadoId(empregadoId)).thenReturn(Arrays.asList(pontoEletronico1, pontoEletronico3));
        when(pontoEletronicoMapper.toMinDto(pontoEletronico1)).thenReturn(pontoEletronicoDTO1);
        when(pontoEletronicoMapper.toMinDto(pontoEletronico3)).thenReturn(pontoEletronicoDTO3);

        List<PontoEletronicoMinDTO> result = service.getAllByEmpregado(empregadoId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(pontoEletronicoDTO1, pontoEletronicoDTO3);

        verify(repository, times(1)).findByEmpregadoId(empregadoId);
        verify(pontoEletronicoMapper, times(1)).toMinDto(pontoEletronico1);
        verify(pontoEletronicoMapper, times(1)).toMinDto(pontoEletronico3);
    }

    @Test
    public void getAllByEmpregadoAndDateShouldReturnListOfPontos() {
        when(repository.findByEmpregadoIdAndData(empregadoId, LocalDate.now().minusDays(2))).thenReturn(Collections.singletonList(pontoEletronico1));
        when(pontoEletronicoMapper.toMinDto(pontoEletronico1)).thenReturn(pontoEletronicoDTO1);

        List<PontoEletronicoMinDTO> result = service.getByDate(empregadoId, LocalDate.now().minusDays(2));

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(pontoEletronicoDTO1);

        verify(repository, times(1)).findByEmpregadoIdAndData(empregadoId, LocalDate.now().minusDays(2));
        verify(pontoEletronicoMapper, times(1)).toMinDto(pontoEletronico1);
    }

    @Test
    public void getPontoByEmpregadoAndDateShouldReturnEmptyListWhenNotFoundDate() {
        when(repository.findByEmpregadoIdAndData(empregadoId, LocalDate.now().minusDays(5))).thenReturn(Collections.emptyList());

        List<PontoEletronicoMinDTO> result = service.getByDate(empregadoId, LocalDate.now().minusDays(5));

        assertThat(result).isEmpty();

        verify(repository, times(1)).findByEmpregadoIdAndData(empregadoId, LocalDate.now().minusDays(5));
    }

    @Test
    public void registrarEntradaShouldCallEmpregadoClientAndContinueWhenEmpregadoExists() {
        when(empregadoClient.obterEmpregado(empregadoId)).thenReturn(empregadoDTO);
        when(repository.save(any(PontoEletronico.class))).thenReturn(pontoEletronico1);
        when(pontoEletronicoMapper.toMinDto(pontoEletronico1)).thenReturn(pontoEletronicoDTO1);

        PontoEletronicoMinDTO result = service.registrarEntrada(empregadoId);

        assertThat(result).isEqualTo(pontoEletronicoDTO1);
        verify(empregadoClient, times(1)).obterEmpregado(empregadoId);
        verify(repository, times(1)).save(any(PontoEletronico.class));
    }

    @Test
    public void registrarEntradaShouldThrowResourceNotFoundExceptionWhenEmpregadoDoesNotExist() {
        Request request = feign.Request.create(feign.Request.HttpMethod.POST, "localhost", new HashMap<>(),
                "Empty".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        when(empregadoClient.obterEmpregado(nonExistingId))
                .thenThrow(new FeignException.FeignClientException.NotFound("NOT FOUND", request, null, null));

        assertThrows(ResourceNotFoundException.class, () -> service.registrarEntrada(nonExistingId));

        verify(empregadoClient, times(1)).obterEmpregado(nonExistingId);
        verify(repository, never()).save(any(PontoEletronico.class));
    }

    @Test
    public void registrarEntradaShouldThrowPontoEletronicoExceptionWhenExitNotExists() {
        when(empregadoClient.obterEmpregado(empregadoId)).thenReturn(empregadoDTO);
        when(repository.findByEmpregadoIdAndDataAndHoraSaidaIsNull(empregadoId, LocalDate.now())).thenReturn(Optional.of(pontoEletronico1));

        assertThrows(PontoEletronicoException.class, () -> service.registrarEntrada(empregadoId));

        verify(empregadoClient, times(1)).obterEmpregado(empregadoId);
        verify(repository, never()).save(any(PontoEletronico.class));
    }

    @Test
    public void registrarSaidaShouldCallEmpregadoClientAndContinueWhenEmpregadoExists() {
        when(empregadoClient.obterEmpregado(empregadoId)).thenReturn(empregadoDTO);
        when(repository.findByEmpregadoIdAndDataAndHoraSaidaIsNull(empregadoId, LocalDate.now())).thenReturn(Optional.of(pontoEletronico1));
        when(pontoEletronicoMapper.toMinDto(any())).thenReturn(pontoEletronicoDTO1);

        PontoEletronicoMinDTO result = service.registrarSaida(empregadoId);

        assertThat(result).isEqualTo(pontoEletronicoDTO1);
        verify(empregadoClient, times(1)).obterEmpregado(empregadoId);
        verify(repository, times(1)).save(any(PontoEletronico.class));
    }

    @Test
    public void registrarSaidaShouldThrowPontoEletronicoExceptionWhenExitNotExists() {
        when(empregadoClient.obterEmpregado(empregadoId)).thenReturn(empregadoDTO);
        when(repository.findByEmpregadoIdAndDataAndHoraSaidaIsNull(empregadoId, LocalDate.now())).thenReturn(Optional.empty());

        assertThrows(PontoEletronicoException.class, () -> service.registrarSaida(empregadoId));

        verify(empregadoClient, times(1)).obterEmpregado(empregadoId);
        verify(repository, never()).save(any(PontoEletronico.class));
    }

    @Test
    public void calcularHorasShouldReturnHorasTrabalhadasComIntervaloAutomatico() {
        pontoEletronico2.setHoraEntrada(LocalDateTime.now().toLocalTime().minusHours(9));
        pontoEletronico2.setHoraSaida(LocalDateTime.now().toLocalTime());

        when(repository.findByEmpregadoIdAndData(empregadoId, LocalDate.now())).thenReturn(List.of(pontoEletronico2));
        Double result = service.calcularHorasTrabalhadas(empregadoId, LocalDate.now());

        assertThat(result).isEqualTo(8.0);
        verify(repository, times(1)).findByEmpregadoIdAndData(empregadoId, LocalDate.now());
    }

    @Test
    public void calcularHorasShouldReturnHorasTrabalhadasSemIntervaloAutomatico() {
        pontoEletronico1.setHoraEntrada(LocalDateTime.now().toLocalTime().minusHours(9));
        pontoEletronico1.setHoraSaida(LocalDateTime.now().toLocalTime().minusHours(5));

        pontoEletronico2.setHoraEntrada(LocalDateTime.now().toLocalTime().minusHours(4));
        pontoEletronico2.setHoraSaida(LocalDateTime.now().toLocalTime());

        when(repository.findByEmpregadoIdAndData(empregadoId, LocalDate.now())).thenReturn(List.of(pontoEletronico1, pontoEletronico2));
        Double result = service.calcularHorasTrabalhadas(empregadoId, LocalDate.now());

        assertThat(result).isEqualTo(8.0);
        verify(repository, times(1)).findByEmpregadoIdAndData(empregadoId, LocalDate.now());
    }

    @Test
    public void calcularHorasExtrasShouldReturnHorasExtras() {
        pontoEletronico1.setHoraEntrada(LocalDateTime.now().toLocalTime().minusHours(11));
        pontoEletronico1.setHoraSaida(LocalDateTime.now().toLocalTime().minusHours(5));

        pontoEletronico2.setHoraEntrada(LocalDateTime.now().toLocalTime().minusHours(4));
        pontoEletronico2.setHoraSaida(LocalDateTime.now().toLocalTime());

        when(repository.findByEmpregadoIdAndData(empregadoId, LocalDate.now())).thenReturn(List.of(pontoEletronico1, pontoEletronico2));
        Double result = service.calcularHorasExtras(empregadoId, LocalDate.now(), Long.parseLong(empregadoDTO.getJornadaDiaria()));

        assertThat(result).isEqualTo(2);
        verify(repository, times(1)).findByEmpregadoIdAndData(empregadoId, LocalDate.now());
    }

    @Test
    public void calcularHorasNegativasShouldReturnHorasExtrasWithIntervalo() {
        pontoEletronico1.setHoraEntrada(LocalDateTime.now().toLocalTime().minusHours(4));
        pontoEletronico1.setHoraSaida(LocalDateTime.now().toLocalTime().minusHours(3));

        pontoEletronico2.setHoraEntrada(LocalDateTime.now().toLocalTime().minusHours(1));
        pontoEletronico2.setHoraSaida(LocalDateTime.now().toLocalTime());

        when(repository.findByEmpregadoIdAndData(empregadoId, LocalDate.now())).thenReturn(List.of(pontoEletronico1, pontoEletronico2));
        Double result = service.calcularHorasExtras(empregadoId, LocalDate.now(), Long.parseLong(empregadoDTO.getJornadaDiaria()));

        assertThat(result).isEqualTo(-6);
        verify(repository, times(1)).findByEmpregadoIdAndData(empregadoId, LocalDate.now());
    }

    @Test
    public void calcularHorasNegativasShouldReturnHorasExtrasWithoutIntervalo() {
        pontoEletronico1.setHoraEntrada(LocalDateTime.now().toLocalTime().minusHours(4));
        pontoEletronico1.setHoraSaida(LocalDateTime.now().toLocalTime().minusHours(3));

        when(repository.findByEmpregadoIdAndData(empregadoId, LocalDate.now())).thenReturn(List.of(pontoEletronico1));
        Double result = service.calcularHorasExtras(empregadoId, LocalDate.now(), Long.parseLong(empregadoDTO.getJornadaDiaria()));

        assertThat(result).isEqualTo(-7);
        verify(repository, times(1)).findByEmpregadoIdAndData(empregadoId, LocalDate.now());
    }
}
