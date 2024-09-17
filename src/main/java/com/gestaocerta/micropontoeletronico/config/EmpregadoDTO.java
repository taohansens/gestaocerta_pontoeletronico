package com.gestaocerta.micropontoeletronico.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpregadoDTO {

    private Long id;
    private String nome;
    private String cpf;
    private DocumentoDTO documento;
    private EnderecoDTO endereco;
    private String sexo;
    private LocalDate nascimento;
    private String jornadaDiaria;
    private String jornadaSemanal;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentoDTO {
        private String tipo;
        private String numero;
        private String orgaoEmissor;
        private LocalDate dataEmissao;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnderecoDTO {
        private String logradouro;
        private String complemento;
        private String bairro;
        private String cidade;
        private String estado;
        private String cep;
    }
}