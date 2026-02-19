package com.barbertime.dto;

public record ClienteResumoDTO(
	    Long id,
	    String nome,
	    String telefone,
	    Integer totalCortes,
	    String modalidade // NOVO, FIEL, VIP, ATRASADO
	) {}