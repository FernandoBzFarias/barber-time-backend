package com.barbertime.dto;

import java.util.List;

public record FinanceiroResumoDTO(
	    Double faturamentoTotal,
	    Double totalComissoes,
	    Double lucroLiquido,
	    Double variacaoPercentual, 
	    List<ComissaoBarbeiroDTO> detalhesBarbeiros,
	    List<EvolucaoMensalDTO> graficoEvolucao) {}
