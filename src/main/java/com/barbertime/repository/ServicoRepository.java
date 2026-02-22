package com.barbertime.repository;

import com.barbertime.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    List<Servico> findByBarbeariaId(Long barbeariaId);
    
    // Busca apenas o que o cliente pode agendar
    List<Servico> findByBarbeariaIdAndAtivoTrue(Long barbeariaId);
}