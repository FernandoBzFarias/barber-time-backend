package com.barbertime.dto;

public record BarbeariaRequestDTO(
	    String nome,
	    String endereco,
	    String cidade,
	    String estado,
	    String telefone,
	    String email,
	    String horarioAbertura,
	    String horarioFechamento,
	    String senhaConfirmacao 
	) {}