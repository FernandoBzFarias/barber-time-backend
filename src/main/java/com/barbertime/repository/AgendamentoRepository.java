package com.barbertime.repository;

import com.barbertime.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // Para a agenda de UM barbeiro específico
    @Query("SELECT a FROM Agendamento a JOIN FETCH a.barbeiro WHERE a.barbeiro.id = :id AND a.data = :data")
    List<Agendamento> findByBarbeiroIdAndData(@Param("id") Long id, @Param("data") LocalDate data);
    
    // Para a agenda de TODOS os barbeiros (Botão "Todos")
    @Query("SELECT a FROM Agendamento a JOIN FETCH a.barbeiro b WHERE a.data = :data AND b.barbeariaId = :barbeariaId")
    List<Agendamento> findByDataAndBarbearia(@Param("data") LocalDate data, @Param("barbeariaId") Long barbeariaId);
    
    boolean existsByBarbeiroIdAndDataAndHorarioAndStatusNot(
            Long barbeiroId, LocalDate data, java.time.LocalTime horario, com.barbertime.entity.StatusAgendamento status);
    
    List<Agendamento> findByClienteIdOrderByDataDesc(Long clienteId);
    List<Agendamento> findFirst10ByClienteIdOrderByDataDesc(Long clienteId);
    
    @Query("SELECT a FROM Agendamento a WHERE a.barbeiro.id = :barbeiroId " +
    	       "AND a.status = 'FINALIZADO' " +
    	       "AND MONTH(a.data) = :mes AND YEAR(a.data) = :ano")
    	List<Agendamento> buscarFinalizadosPorPeriodo(Long barbeiroId, int mes, int ano);
    
    @Query("SELECT SUM(a.valor) FROM Agendamento a " +
    	       "WHERE a.barbeiro.barbeariaId = :barbeariaId " +
    	       "AND a.status = 'FINALIZADO' " +
    	       "AND a.data >= :dataInicio")
    	Double faturamentoTotalNoPeriodo(@Param("barbeariaId") Long barbeariaId, @Param("dataInicio") LocalDate dataInicio);
    
    @Query("SELECT SUM(a.valor) FROM Agendamento a " +
    	       "WHERE a.barbeiro.barbeariaId = :barbeariaId " +
    	       "AND a.status = 'FINALIZADO' " +
    	       "AND MONTH(a.data) = :mes AND YEAR(a.data) = :ano")
    	Double buscarFaturamentoMensalUnidade(@Param("barbeariaId") Long barbeariaId, 
    	                                      @Param("mes") int mes, 
    	                                      @Param("ano") int ano);
}
