package com.barbertime.service;

import com.barbertime.entity.Servico;
import com.barbertime.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    public List<Servico> listarPorBarbearia(Long id) {
        return servicoRepository.findByBarbeariaId(id);
    }

    public Servico salvar(Servico servico) {
        if (servico.getAtivo() == null) servico.setAtivo(true);
        return servicoRepository.save(servico);
    }

    public void alternarStatus(Long id) {
        Servico s = servicoRepository.findById(id).orElseThrow();
        s.setAtivo(!s.getAtivo()); // Inverte o estado atual
        servicoRepository.save(s);
    }
}
