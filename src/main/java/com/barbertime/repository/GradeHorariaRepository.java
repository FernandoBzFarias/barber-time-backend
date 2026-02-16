package com.barbertime.repository;

import com.barbertime.entity.GradeHoraria;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import java.time.LocalTime;
import java.util.List;

public interface GradeHorariaRepository extends JpaRepository<GradeHoraria, Long> {
    List<GradeHoraria> findByBarbeiroIdOrderByHorarioAsc(Long barbeiroId);
    
    boolean existsByBarbeiroIdAndHorario(Long barbeiroId, LocalTime horario);
    @Modifying
    @Transactional
    void deleteAllByBarbeiroId(Long barbeiroId);
}
