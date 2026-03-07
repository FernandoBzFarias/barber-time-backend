package com.barbertime.controller;

import com.barbertime.dto.DashboardHomeDTO;
import com.barbertime.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private AgendamentoService service;

    @GetMapping("/resumo-operacional")
    public ResponseEntity<DashboardHomeDTO> getResumoOperacional() {
        return ResponseEntity.ok(service.obterDashboardPrincipal());
    }
}