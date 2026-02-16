package com.barbertime.repository;

import com.barbertime.entity.Agendamento;
import com.barbertime.entity.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    
    // Verifica se já existe agendamento para aquele barbeiro, data e hora (desconsiderando cancelados)
    boolean existsByBarbeiroIdAndDataAndHorarioAndStatusNot(
        Long barbeiroId, LocalDate data, LocalTime horario, StatusAgendamento status
    );

    // Busca agendamentos de um dia para mostrar o que está ocupado
    List<Agendamento> findByBarbeiroIdAndData(Long barbeiroId, LocalDate data);
}
