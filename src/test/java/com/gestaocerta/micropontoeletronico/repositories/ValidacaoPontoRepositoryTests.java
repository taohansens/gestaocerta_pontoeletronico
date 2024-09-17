package com.gestaocerta.micropontoeletronico.repositories;

import com.gestaocerta.micropontoeletronico.entities.ValidacaoPonto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
public class ValidacaoPontoRepositoryTests {

    @Autowired
    private ValidacaoPontoRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalPontos;

    @BeforeEach
    void setUp() throws Exception {
        repository.deleteAll();
        ValidacaoPonto validacaoPonto = new ValidacaoPonto();
        validacaoPonto.setEmpregadoId(1L);
        validacaoPonto.setPontoEletronicoId(1L);
        repository.save(validacaoPonto);

        existingId = validacaoPonto.getId();
        nonExistingId = 1000L;
        countTotalPontos = repository.count();
    }

    @Test
    public void countShouldReturnCorrectNumberOfEmpregados() {
        assertThat(repository.count()).isEqualTo(countTotalPontos);
    }

    @Test
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
        Optional<ValidacaoPonto> result = repository.findById(existingId);
        assertThat(result).isPresent();
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        Optional<ValidacaoPonto> result = repository.findById(nonExistingId);
        assertThat(result).isEmpty();
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        assertThat(repository.existsById(existingId)).isTrue();
        repository.deleteById(existingId);

        Optional<ValidacaoPonto> result = repository.findById(existingId);
        assertThat(result).isEmpty();

        assertThat(repository.count()).isEqualTo(countTotalPontos - 1);
    }

    @Test
    public void deleteShouldNotThrowExceptionWhenIdDoesNotExist() {
        assertThat(repository.existsById(nonExistingId)).isFalse();
        repository.deleteById(nonExistingId);
        assertThat(repository.count()).isEqualTo(countTotalPontos);
    }

    @Test
    public void insertShouldInsertObject() {
        ValidacaoPonto validacaoPonto = new ValidacaoPonto();
        validacaoPonto.setEmpregadoId(4L);
        validacaoPonto.setPontoEletronicoId(4L);
        validacaoPonto.setHorasExtras(4.0);
        validacaoPonto.setDataTrabalhada(LocalDateTime.now());
        validacaoPonto.setCodigoSituacao("100");
        validacaoPonto.setComentario("OK");
        ValidacaoPonto insert = repository.save(validacaoPonto);

        assertThat(repository.count()).isEqualTo(countTotalPontos + 1);
        assertThat(insert).isNotNull();
        assertThat(insert.getId()).isNotNull();
        assertThat(insert.getEmpregadoId()).isEqualTo(4L);
        assertThat(insert.getHorasExtras()).isEqualTo(4.0);
    }
}
