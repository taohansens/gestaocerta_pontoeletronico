package com.taohansen.mod.pontoeletronico.controllers;

import com.taohansen.mod.pontoeletronico.dto.PontoEletronicoMinDTO;
import com.taohansen.mod.pontoeletronico.services.PontoEletronicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ponto/{empregadoId}")
public class PontoEletronicoController {
    private final PontoEletronicoService pontoEletronicoService;

    @GetMapping
    public ResponseEntity<List<PontoEletronicoMinDTO>> getAll(@PathVariable Long empregadoId) {
        List<PontoEletronicoMinDTO> pontos = pontoEletronicoService.getAllByEmpregado(empregadoId);
        return ResponseEntity.ok(pontos);
    }
    @GetMapping("/consulta")
        public ResponseEntity<List<PontoEletronicoMinDTO>> getByDate(@PathVariable Long empregadoId, @RequestParam LocalDate data) {
            List<PontoEletronicoMinDTO> pontos = pontoEletronicoService.getByDate(empregadoId, data);
            return ResponseEntity.ok(pontos);
    }

    @GetMapping("/consulta/{id}")
    public ResponseEntity<PontoEletronicoMinDTO> getByDate(@PathVariable Long id) {
        PontoEletronicoMinDTO ponto = pontoEletronicoService.getById(id);
        return ResponseEntity.ok(ponto);
    }

    @PostMapping("/entrada")
    public ResponseEntity<PontoEletronicoMinDTO> registrarEntrada(@PathVariable Long empregadoId) {
        PontoEletronicoMinDTO ponto = pontoEletronicoService.registrarEntrada(empregadoId);
        return ResponseEntity.status(201).body(ponto);
    }

    @PostMapping("/saida")
    public ResponseEntity<PontoEletronicoMinDTO> registrarSaida(@PathVariable Long empregadoId) {
        PontoEletronicoMinDTO ponto = pontoEletronicoService.registrarSaida(empregadoId);
        return ResponseEntity.ok(ponto);
    }
}
