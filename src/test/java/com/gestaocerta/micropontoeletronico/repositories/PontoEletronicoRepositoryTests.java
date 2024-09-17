package com.gestaocerta.micropontoeletronico.repositories;

import com.gestaocerta.micropontoeletronico.entities.PontoEletronico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
public class PontoEletronicoRepositoryTests {

    @Autowired
    private PontoEletronicoRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalPontos;

    @BeforeEach
    void setUp() throws Exception {
        repository.deleteAll();
        PontoEletronico pontoEletronico = new PontoEletronico();
        pontoEletronico.setEmpregadoId(1L);
        pontoEletronico.setData(LocalDate.now());
        pontoEletronico.setHoraEntrada(LocalDateTime.now().toLocalTime());
        pontoEletronico.setHoraSaida(LocalDateTime.now().toLocalTime());
        pontoEletronico.setHorasExtras(10.0);
        repository.save(pontoEletronico);

        PontoEletronico pontoEletronico2 = new PontoEletronico();
        pontoEletronico2.setEmpregadoId(2L);
        pontoEletronico2.setData(LocalDate.now());
        pontoEletronico2.setHoraEntrada(LocalDateTime.now().toLocalTime().minusHours(10));
        pontoEletronico2.setHoraSaida(LocalDateTime.now().toLocalTime().minusHours(5));
        pontoEletronico2.setHorasExtras(2.0);
        repository.save(pontoEletronico2);

        existingId = pontoEletronico.getId();
        nonExistingId = 1000L;
        countTotalPontos = repository.count();
    }

    @Test
    public void countShouldReturnCorrectNumberOfEmpregados() {
        assertThat(repository.count()).isEqualTo(countTotalPontos);
    }

    @Test
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
        Optional<PontoEletronico> result = repository.findById(existingId);
        assertThat(result).isPresent();
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        Optional<PontoEletronico> result = repository.findById(nonExistingId);
        assertThat(result).isEmpty();
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        assertThat(repository.existsById(existingId)).isTrue();
        repository.deleteById(existingId);

        Optional<PontoEletronico> result = repository.findById(existingId);
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
        PontoEletronico pontoEletronico = new PontoEletronico();
        pontoEletronico.setEmpregadoId(1L);
        pontoEletronico.setData(LocalDate.now());
        pontoEletronico.setHoraEntrada(LocalDateTime.now().toLocalTime());
        pontoEletronico.setHoraSaida(LocalDateTime.now().toLocalTime());
        pontoEletronico.setHorasExtras(10.0);
        PontoEletronico insert = repository.save(pontoEletronico);

        assertThat(repository.count()).isEqualTo(countTotalPontos + 1);
        assertThat(insert).isNotNull();
        assertThat(insert.getId()).isNotNull();
        assertThat(insert.getEmpregadoId()).isEqualTo(1L);
        assertThat(insert.getHorasExtras()).isEqualTo(10.0);
    }
}
