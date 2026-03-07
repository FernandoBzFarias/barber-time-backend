package com.barbertime.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.barbertime.dto.BarbeiroPublicoDTO;
import com.barbertime.dto.HorarioDisponivelDTO;
import com.barbertime.dto.NovoAgendamentoDTO;
import com.barbertime.entity.Barbeiro;
import com.barbertime.repository.BarbeiroRepository;
import com.barbertime.service.AgendamentoService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/publico")
@CrossOrigin(origins = "*") // Permite que qualquer um acesse
public class PublicController {

    @Autowired
    private AgendamentoService agendamentoService;
    
    @Autowired
    private BarbeiroRepository barbeiroRepository;

    // 1. Busca os dados do barbeiro pelo SLUG (para montar a tela inicial do cliente)
    @GetMapping("/barbeiro/{slug}")
    public ResponseEntity<BarbeiroPublicoDTO> buscarPorSlug(@PathVariable String slug) {
        Barbeiro b = barbeiroRepository.findBySlug(slug)
            .orElseThrow(() -> new EntityNotFoundException("Barbearia não encontrada"));
        
        // Retorna nome, foto e serviços (você pode criar um DTO para isso)
        return ResponseEntity.ok(new BarbeiroPublicoDTO(b.getId(), b.getNome(), b.getServicos()));
    }

    // 2. Lista horários disponíveis (Usa o método que você JÁ TEM no Service)
    @GetMapping("/horarios")
    public ResponseEntity<List<HorarioDisponivelDTO>> listarHorarios(
            @RequestParam Long barbeiroId, 
            @RequestParam LocalDate data) {
        return ResponseEntity.ok(agendamentoService.listarDisponibilidade(barbeiroId, data));
    }

    // 3. Salva o agendamento (Usa o método 'criarAgendamento' que você JÁ TEM)
    @PostMapping("/agendar")
    public ResponseEntity<String> agendarPublico(@RequestBody NovoAgendamentoDTO dto) {
        return ResponseEntity.ok(agendamentoService.criarAgendamento(dto));
    }
}
