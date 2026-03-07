package com.barbertime.dto;

import java.util.List;
import java.util.Map;

public record RelatorioCompletoDTO(
    long totalAtendimentos,
    double ticketMedio,
    double taxaRetorno,
    double crescimentoAtendimentos, // Porcentagem (+18%)
    List<FaturamentoMensalDTO> faturamentoMensal,
    Map<String, Double> servicosMaisRealizados // Para o gráfico de pizza
) {}

