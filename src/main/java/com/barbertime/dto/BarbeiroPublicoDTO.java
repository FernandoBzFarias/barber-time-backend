package com.barbertime.dto;

import java.util.List;

public record BarbeiroPublicoDTO(
    Long id,
    String nome,
    List<String> servicos
) {}
