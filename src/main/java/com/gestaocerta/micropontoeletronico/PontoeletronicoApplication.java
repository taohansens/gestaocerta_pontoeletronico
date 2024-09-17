package com.gestaocerta.micropontoeletronico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PontoeletronicoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PontoeletronicoApplication.class, args);
    }

}
