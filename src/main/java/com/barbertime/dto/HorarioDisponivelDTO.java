package com.barbertime.dto;

import java.time.LocalTime;

public class HorarioDisponivelDTO {
	private LocalTime horario;
    private boolean disponivel;

    public HorarioDisponivelDTO(LocalTime horario, boolean disponivel) {
        this.horario = horario;
        this.disponivel = disponivel;
    }

    // Getters
    public LocalTime getHorario() { return horario; }
    public boolean isDisponivel() { return disponivel; }
}

