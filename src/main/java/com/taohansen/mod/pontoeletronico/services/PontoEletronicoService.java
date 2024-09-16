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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PontoEletronicoService {
    private final EmpregadoClient empregadoClient;
    private final PontoEletronicoRepository pontoEletronicoRepository;
    private final PontoEletronicoMapper pontoEletronicoMapper;

    private final Duration DURACAO_INTERVALO_PADRAO = Duration.ofHours(1);

    public List<PontoEletronicoMinDTO> getAllByEmpregado(Long empregadoId) {
        List<PontoEletronico> ponto = pontoEletronicoRepository.findByEmpregadoId(empregadoId);
        return ponto.stream()
                .map(pontoEletronicoMapper::toMinDto)
                .collect(Collectors.toList());
    }

    public List<PontoEletronicoMinDTO> getByDate(Long empregadoId, LocalDate data) {
        List<PontoEletronico> list = pontoEletronicoRepository.findByEmpregadoIdAndData(empregadoId, data);
        return list.stream()
                .map(pontoEletronicoMapper::toMinDto)
                .collect(Collectors.toList());
    }

    public PontoEletronicoMinDTO getById(Long id) {
        Optional<PontoEletronico> obj = pontoEletronicoRepository.findById(id);
        PontoEletronico entity = obj.orElseThrow(() -> new ResourceNotFoundException(String.format("Ponto %d não encontrado.", id)));
        return pontoEletronicoMapper.toMinDto(entity);
    }

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

        Double horasTrabalhadas = calcularHorasTrabalhadas(empregadoId, hoje);
        Double horasExtras = calcularHorasExtras(empregadoId, hoje, Long.parseLong(empregado.getJornadaDiaria()));
        ponto.setHorasTrabalhadas(horasTrabalhadas);
        ponto.setHorasExtras(horasExtras);

        PontoEletronico entity = pontoEletronicoRepository.save(ponto);
        return pontoEletronicoMapper.toMinDto(entity);
    }

    public Double calcularHorasTrabalhadas(Long empregadoId, LocalDate data) {
        List<PontoEletronico> pontosDoDia = pontoEletronicoRepository.findByEmpregadoIdAndData(empregadoId, data);

        Duration diftempo = Duration.ZERO;

        for (PontoEletronico ponto : pontosDoDia) {
            if (ponto.getHoraEntrada() != null && ponto.getHoraSaida() != null) {
                diftempo = diftempo.plus(Duration.between(
                        ponto.getHoraEntrada().withSecond(0).withNano(0),
                        ponto.getHoraSaida().withSecond(0).withNano(0))
                );
            }
        }

        if (pontosDoDia.size() == 1 && diftempo.toHours() >= 6) {
            diftempo = diftempo.minus(DURACAO_INTERVALO_PADRAO);
        }

        long diftempoMinutes = diftempo.toMinutes();
        double horas = (double) diftempoMinutes / 6 * 0.1;
        BigDecimal horasTrabalhadas = new BigDecimal(horas).setScale(1, RoundingMode.DOWN);

        return horasTrabalhadas.doubleValue();
    }

    public Double calcularHorasExtras(Long empregadoId, LocalDate data, Long cargaHoraria) {
        Double horasTrabalhadas = calcularHorasTrabalhadas(empregadoId, data);

        double diferenca = horasTrabalhadas - cargaHoraria;
        BigDecimal horasExtras = new BigDecimal(diferenca).setScale(1, RoundingMode.DOWN);
        return horasExtras.doubleValue();
    }
}
