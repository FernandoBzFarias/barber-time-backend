package com.barbertime.dto;

public record ComissaoBarbeiroDTO(
	    Long barbeiroId,
	    String nome,
	    Double faturamento,
	    Double percentual,
	    Double valorAPagar) {}