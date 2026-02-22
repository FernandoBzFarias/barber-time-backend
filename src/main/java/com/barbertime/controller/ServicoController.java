package com.barbertime.controller;

import com.barbertime.entity.Barbeiro;
import com.barbertime.entity.Servico;
import com.barbertime.repository.BarbeiroRepository;
import com.barbertime.service.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;
    @Autowired
    private BarbeiroRepository barbeiroRepository;

    @GetMapping
    public ResponseEntity<List<Servico>> listar() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Barbeiro logado = barbeiroRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(servicoService.listarPorBarbearia(logado.getBarbeariaId()));
    }

    @PostMapping
    public ResponseEntity<Servico> salvar(@RequestBody Servico servico) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Barbeiro logado = barbeiroRepository.findByEmail(email).orElseThrow();
        servico.setBarbeariaId(logado.getBarbeariaId());
        return ResponseEntity.ok(servicoService.salvar(servico));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alternarStatus(@PathVariable Long id) {
        servicoService.alternarStatus(id);
        return ResponseEntity.noContent().build();
    }
}
