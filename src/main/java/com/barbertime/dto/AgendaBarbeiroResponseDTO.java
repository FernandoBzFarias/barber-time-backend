package com.barbertime.dto;

import java.time.LocalTime;

public record AgendaBarbeiroResponseDTO(
	    Long id,
	    LocalTime horario,
	    String clienteNome,
	    String clienteTelefone,
	    String servico,
	    Double valor,
	    String status,
	    String linkWhatsApp,
	    String nomeBarbeiro) {

	public Long id() {
		return id;
	}

	public LocalTime horario() {
		return horario;
	}

	public String clienteNome() {
		return clienteNome;
	}

	public String clienteTelefone() {
		return clienteTelefone;
	}

	public String servico() {
		return servico;
	}

	public Double valor() {
		return valor;
	}

	public String status() {
		return status;
	}

	public String linkWhatsApp() {
		return linkWhatsApp;
	}

	public String nomeBarbeiro() {
		return nomeBarbeiro;
	}
	
}