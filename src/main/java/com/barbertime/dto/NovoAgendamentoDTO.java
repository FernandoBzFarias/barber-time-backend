package com.barbertime.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class NovoAgendamentoDTO {
	@NotNull(message = "O barbeiro deve ser informado")
    private Long barbeiroId;

    @NotNull(message = "A data deve ser informada")
    @FutureOrPresent(message = "A data não pode ser no passado")
    private LocalDate data;

    @NotNull(message = "O horário deve ser informado")
    private LocalTime horario;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100)
    private String clienteNome;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone inválido. Use apenas números com DDD")
    private String clienteTelefone;

    // Getters e Setters
    public Long getBarbeiroId() { return barbeiroId; }
    public LocalDate getData() { return data; }
    public LocalTime getHorario() { return horario; }
    public String getClienteNome() { return clienteNome; }
    public String getClienteTelefone() { return clienteTelefone; }
}
