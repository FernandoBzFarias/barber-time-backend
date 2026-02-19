package com.barbertime.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record HistoricoAgendamentoDTO(
	    LocalDate data,
	    LocalTime horario,
	    String servico,
	    Double valor,
	    String status
	) {}
