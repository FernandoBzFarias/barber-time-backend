package com.barbertime.dto;
public record DashboardHomeDTO(
    CorteInfoDTO corteAtual,
    CorteInfoDTO proximoCorte,
    long totalAtendimentosHoje
) {
    public record CorteInfoDTO(String nomeCliente, String servico, String horario) {}
}
