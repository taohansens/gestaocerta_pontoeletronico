package com.taohansen.mod.pontoeletronico.controllers;

import com.taohansen.mod.pontoeletronico.controllers.exceptions.StandardError;
import com.taohansen.mod.pontoeletronico.dto.PontoEletronicoMinDTO;
import com.taohansen.mod.pontoeletronico.services.PontoEletronicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ponto/")
public class PontoEletronicoController {
    private final PontoEletronicoService pontoEletronicoService;

    @Operation(summary = "Retorna a lista de todos os pontos registrados para o ID do funcionário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retorna a lista dos pontos, vazia ou não",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = PontoEletronicoMinDTO.class)))})
    })
    @GetMapping(value = "/{empregadoId}")
    public ResponseEntity<List<PontoEletronicoMinDTO>> getAll(
            @PathVariable
            @Parameter(description = "Id de cadastro do empregado doméstico", required = true)
            Long empregadoId) {
        List<PontoEletronicoMinDTO> pontos = pontoEletronicoService.getAllByEmpregado(empregadoId);
        return ResponseEntity.ok(pontos);
    }

    @Operation(summary = "Retorna a lista dos pontos registrados para o funcionário para a data informada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Retorna a lista do registro de pontos para a data, vazia ou não",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = PontoEletronicoMinDTO.class)))})
    })
    @GetMapping("/{empregadoId}/consulta")
    public ResponseEntity<List<PontoEletronicoMinDTO>> getByDate(
            @Parameter(description = "Id de cadastro do empregado doméstico", required = true)
            @PathVariable Long empregadoId,
            @Parameter(description = "Data a ser consultada (dd/mm/aaaa)", required = true)
            @RequestParam LocalDate data) {
        List<PontoEletronicoMinDTO> pontos = pontoEletronicoService.getByDate(empregadoId, data);
        return ResponseEntity.ok(pontos);
    }

    @Operation(summary = "Retorna o registro de ponto pelo Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o registro do ponto pelo ID informado",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PontoEletronicoMinDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "Registro de ponto não localizado",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class))})})
    @GetMapping("/consulta/{id}")
    public ResponseEntity<PontoEletronicoMinDTO> getById(
            @Parameter(description = "Id de registro do ponto do eletrônico no banco de dados", required = true)
            @PathVariable Long id) {
        PontoEletronicoMinDTO ponto = pontoEletronicoService.getById(id);
        return ResponseEntity.ok(ponto);
    }

    @Operation(summary = "Registro de entrada de ponto para o funcionário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrada do ponto registrada com sucesso",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PontoEletronicoMinDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "Empregado não encontrado.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class))}),
            @ApiResponse(responseCode = "400", description = "Existe uma entrada registrada sem saída. Não é possível registrar duas entradas seguidas",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class))})
    })
    @PostMapping("/{empregadoId}/entrada")
    public ResponseEntity<PontoEletronicoMinDTO> registrarEntrada(
            @Parameter(description = "Id de registro do empregado no banco de dados", required = true)
            @PathVariable Long empregadoId) {
        PontoEletronicoMinDTO ponto = pontoEletronicoService.registrarEntrada(empregadoId);
        return ResponseEntity.status(201).body(ponto);
    }

    @PostMapping("/{empregadoId}/saida")
    @Operation(summary = "Registro de saída de ponto para o funcionário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Saída do ponto registrada com sucesso",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PontoEletronicoMinDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "Empregado não encontrado.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class))}),
            @ApiResponse(responseCode = "400", description = "Existe uma saída registrada. Não é possível sair sem existir uma entrada registrada para o dia",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class))})
    })
    public ResponseEntity<PontoEletronicoMinDTO> registrarSaida(
            @Parameter(description = "Id de registro do empregado no banco de dados", required = true)
            @PathVariable Long empregadoId) {
        PontoEletronicoMinDTO ponto = pontoEletronicoService.registrarSaida(empregadoId);
        return ResponseEntity.ok(ponto);
    }
}
