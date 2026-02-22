package com.barbertime.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.barbertime.entity.Despesa;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long> {
    
    @Query("SELECT d FROM Despesa d WHERE d.barbeariaId = :id " +
           "AND MONTH(d.data) = :mes AND YEAR(d.data) = :ano")
    List<Despesa> buscarPorMesEAno(Long id, int mes, int ano);

    @Query("SELECT SUM(d.valor) FROM Despesa d WHERE d.barbeariaId = :id " +
           "AND MONTH(d.data) = :mes AND YEAR(d.data) = :ano")
    Double somarDespesasDoMes(Long id, int mes, int ano);
}
