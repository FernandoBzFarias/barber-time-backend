package com.barbertime.dto;

import java.time.LocalTime;
import java.util.List;

public class GradeRequestDTO {
    private Long barbeiroId;
    private List<LocalTime> horarios;

    // Getters e Setters
    public Long getBarbeiroId() { return barbeiroId; }
    public void setBarbeiroId(Long barbeiroId) { this.barbeiroId = barbeiroId; }
    public List<LocalTime> getHorarios() { return horarios; }
    public void setHorarios(List<LocalTime> horarios) { this.horarios = horarios; }
}
