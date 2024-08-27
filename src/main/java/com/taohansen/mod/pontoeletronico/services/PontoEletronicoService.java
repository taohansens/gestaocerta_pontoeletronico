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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PontoEletronicoService {
    private final EmpregadoClient empregadoClient;
    private final PontoEletronicoRepository pontoEletronicoRepository;
    private final PontoEletronicoMapper pontoEletronicoMapper;

    private final Duration DURACAO_INTERVALO_PADRAO = Duration.ofHours(1);

    public PontoEletronicoMinDTO registrarEntrada(Long empregadoId) {
        EmpregadoDTO empregado;
        try {
            empregado = empregadoClient.obterEmpregado(empregadoId);
        } catch (FeignException.FeignClientException.NotFound e) {
            throw new ResourceNotFoundException("Empregado com ID " + empregadoId + " não encontrado");
        }

        if (!empregadoId.equals(empregado.getId())) {
            throw new ResourceNotFoundException("Empregado com ID " + empregadoId + " não encontrado");
        }

        LocalDate hoje = LocalDate.now();
        Optional<PontoEletronico> pontoEmAberto = pontoEletronicoRepository
                .findByEmpregadoIdAndDataAndHoraSaidaIsNull(empregadoId, hoje);

        if (pontoEmAberto.isPresent())
            throw new PontoEletronicoException("Já existe uma entrada registrada sem saída.");

        LocalTime agora = LocalTime.now();

        PontoEletronico ponto = new PontoEletronico();
        ponto.setEmpregadoId(empregadoId);
        ponto.setData(hoje);
        ponto.setHoraEntrada(agora);

        PontoEletronico entity = pontoEletronicoRepository.save(ponto);
        return pontoEletronicoMapper.toMinDto(entity);
    }

    public PontoEletronicoMinDTO registrarSaida(Long empregadoId) {
        EmpregadoDTO empregado;
        try {
            empregado = empregadoClient.obterEmpregado(empregadoId);
        } catch (FeignException.FeignClientException.NotFound e) {
            throw new ResourceNotFoundException("Empregado com ID " + empregadoId + " não encontrado");
        }

        if (!empregadoId.equals(empregado.getId())) {
            throw new ResourceNotFoundException("Empregado com ID " + empregadoId + " não encontrado");
        }

        LocalDate hoje = LocalDate.now();
        PontoEletronico ponto = pontoEletronicoRepository.findByEmpregadoIdAndDataAndHoraSaidaIsNull(empregado.getId(), hoje)
                .orElseThrow(() -> new PontoEletronicoException("Registro de ponto de entrada não encontrado para hoje"));

        ponto.setHoraSaida(LocalTime.now());

        Duration horasTrabalhadas = calcularHorasTrabalhadas(empregadoId, hoje);
        Duration horasExtras = calcularHorasExtras(empregadoId, hoje, Long.parseLong(empregado.getJornadaDiaria()));
        ponto.setHorasTrabalhadas(horasTrabalhadas);
        ponto.setHorasExtras(horasExtras);

        PontoEletronico entity = pontoEletronicoRepository.save(ponto);
        return pontoEletronicoMapper.toMinDto(entity);
    }

    public List<PontoEletronico> listarPontosNaDataPorEmpregado(Long empregadoId) {
        return pontoEletronicoRepository.findByEmpregadoIdAndData(empregadoId, LocalDate.now());
    }

    public List<PontoEletronico> listarPontos(Long empregadoId) {
        return pontoEletronicoRepository.findByEmpregadoId(empregadoId);
    }

    public Duration calcularHorasTrabalhadas(Long empregadoId, LocalDate data) {
        List<PontoEletronico> pontosDoDia = pontoEletronicoRepository.findByEmpregadoIdAndData(empregadoId, data);

        Duration horasTrabalhadas = Duration.ZERO;

        for (PontoEletronico ponto : pontosDoDia) {
            if (ponto.getHoraEntrada() != null && ponto.getHoraSaida() != null) {
                horasTrabalhadas = horasTrabalhadas.plus(Duration.between(ponto.getHoraEntrada(), ponto.getHoraSaida()));
            }
        }

        if (pontosDoDia.size() == 2 && horasTrabalhadas.toHours() >= 6) {
            horasTrabalhadas = horasTrabalhadas.minus(DURACAO_INTERVALO_PADRAO);
        }

        return horasTrabalhadas;
    }

    public Duration calcularHorasExtras(Long empregadoId, LocalDate data, Long cargaHoraria) {
        Duration horasTrabalhadas = calcularHorasTrabalhadas(empregadoId, data);

        if (horasTrabalhadas.toHours() > cargaHoraria) {
            return horasTrabalhadas.minusHours(cargaHoraria);
        }

        return Duration.ZERO;
    }
}
