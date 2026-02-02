package com.barbertime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barbertime.dto.CadastroBarbeiroDTO;
import com.barbertime.dto.LoginBarbeiroDTO;
import com.barbertime.entity.Barbeiro;
import com.barbertime.service.BarbeiroService;

@RestController
@RequestMapping("/api/barbeiros")
@CrossOrigin(origins = "*")
public class BarbeiroController {
	@Autowired
    private BarbeiroService service;

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody CadastroBarbeiroDTO dto) {
        Barbeiro barbeiro = service.cadastrar(dto);
        return ResponseEntity.ok(barbeiro);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginBarbeiroDTO dto) {
        Barbeiro barbeiro = service.login(dto);
        return ResponseEntity.ok(barbeiro);
    }
}
