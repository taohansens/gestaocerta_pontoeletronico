package com.gestaocerta.micropontoeletronico.services;

import com.gestaocerta.micropontoeletronico.entities.ValidacaoPonto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidacaoProducer {
    private final RabbitTemplate rabbitTemplate;
    public void sendValidacaoPonto(ValidacaoPonto validacaoPonto) {
        rabbitTemplate.convertAndSend("filaBancoDeHoras", validacaoPonto);
    }
}
