package com.taohansen.mod.pontoeletronico.controllers;

import com.taohansen.mod.pontoeletronico.controllers.exceptions.StandardError;
import com.taohansen.mod.pontoeletronico.dto.ValidacaoPontoDTO;
import com.taohansen.mod.pontoeletronico.dto.ValidacaoPontoInsertDTO;
import com.taohansen.mod.pontoeletronico.services.ValidacaoPontoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/validar")
public class ValidacaoPontoController {
    private final ValidacaoPontoService validacaoPontoService;

    @PostMapping
    @Operation(summary = "Validação de ponto pelo empregador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ponto validado com sucesso",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ValidacaoPontoDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "Ponto não encontrado.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class))}),
            @ApiResponse(responseCode = "400", description = "Ponto já validado",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class))})
    })
    public ResponseEntity<ValidacaoPontoDTO> validarById(
            @RequestBody ValidacaoPontoInsertDTO validacaoDTO) {
        ValidacaoPontoDTO ponto = validacaoPontoService.validarById(validacaoDTO);
        return ResponseEntity.status(201).body(ponto);
    }
}
