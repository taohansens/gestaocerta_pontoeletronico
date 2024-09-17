package com.gestaocerta.micropontoeletronico.controllers.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor @AllArgsConstructor
public class FieldMessage {
    private String fieldName;
    private String message;
}