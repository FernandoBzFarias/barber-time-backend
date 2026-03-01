package com.barbertime.dto;

public record ResumoDiarioDTO(
	    Integer totalClientes,
	    Double faturamentoTotal,
	    String primeiroHorario,
	    String ultimoHorario
	) {}