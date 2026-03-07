package com.barbertime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barbertime.dto.MarketingDTO;
import com.barbertime.service.AgendamentoService;

@RestController
@RequestMapping("/api/marketing")
public class MarketingController {

    @Autowired
    private AgendamentoService service;

    @GetMapping
    public ResponseEntity<MarketingDTO> getMarketingData() {
        return ResponseEntity.ok(service.obterDadosMarketing());
    }
}