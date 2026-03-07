package com.barbertime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barbertime.dto.RelatorioCompletoDTO;
import com.barbertime.entity.Barbeiro;
import com.barbertime.repository.BarbeiroRepository;
import com.barbertime.service.AgendamentoService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private BarbeiroRepository barbeiroRepository;

    @GetMapping("/geral")
    public ResponseEntity<RelatorioCompletoDTO> getRelatorio(@RequestParam String pinFinanceiro) {
        // 1. Pega o usuário logado
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Barbeiro barbeiro = barbeiroRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Barbeiro não encontrado"));

        // 2. Valida o PIN (Segurança da imagem que você enviou)
        if (barbeiro.getSenhaFinanceira() == null || !barbeiro.getSenhaFinanceira().equals(pinFinanceiro)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 3. Retorna os dados para os gráficos
        return ResponseEntity.ok(agendamentoService.obterRelatorioGeral(barbeiro.getBarbeariaId()));
    }
}