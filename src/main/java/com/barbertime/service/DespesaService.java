package com.barbertime.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barbertime.entity.Despesa;
import com.barbertime.repository.DespesaRepository;

@Service
public class DespesaService {

    @Autowired
    private DespesaRepository despesaRepository;

    public List<Despesa> listarPorMes(Long barbeariaId, int mes, int ano) {
        return despesaRepository.buscarPorMesEAno(barbeariaId, mes, ano);
    }

    public Despesa salvar(Despesa despesa) {
        return despesaRepository.save(despesa);
    }

    public void deletar(Long id) {
        despesaRepository.deleteById(id);
    }

    public Double calcularTotalMes(Long barbeariaId, int mes, int ano) {
        Double total = despesaRepository.somarDespesasDoMes(barbeariaId, mes, ano);
        return (total != null) ? total : 0.0;
    }
}
