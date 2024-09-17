package com.taohansen.mod.pontoeletronico.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "micro-empregados")
public interface EmpregadoClient {
    @GetMapping("/empregados/{id}")
    EmpregadoDTO obterEmpregado(@PathVariable("id") Long id);
}
