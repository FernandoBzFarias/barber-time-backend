package com.barbertime.dto;
import java.util.List;

public record ClienteDetalheDTO(
	    Long id,
	    String nome,
	    String telefone,
	    String modalidade,
	    Integer totalCortes,
	    Integer quantidadeAtrasos,
	    Double valorMedioGasto,
	    String notasTecnicas,
	    List<HistoricoAgendamentoDTO> historico
	) {}

