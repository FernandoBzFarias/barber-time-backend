package com.barbertime.dto;

public record BarbeiroConfigDTO(
	    String nome,
	    String email,
	    String telefone,
	    String novaSenha,          // Preenchido apenas se ele quiser mudar a senha
	    boolean notificarAgendamentos,
	    boolean notificarLembretes,
	    boolean notificarCancelamentos
	) {}
