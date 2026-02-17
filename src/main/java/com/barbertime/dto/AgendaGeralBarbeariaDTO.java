package com.barbertime.dto;

import java.util.List;

public record AgendaGeralBarbeariaDTO(
    Long barbeiroId,
    String nomeBarbeiro,
    List<AgendaBarbeiroResponseDTO> agendamentos
) {}