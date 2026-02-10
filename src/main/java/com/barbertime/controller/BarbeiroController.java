package com.barbertime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.barbertime.dto.BarbeiroRespondeDTO;
import com.barbertime.dto.CadastroBarbeiroDTO;
import com.barbertime.dto.LoginBarbeiroDTO;
import com.barbertime.dto.RedefinirSenhaDTO;
import com.barbertime.dto.ResetSenhaDTO;
import com.barbertime.service.BarbeiroService;

@RestController
@RequestMapping("/api/barbeiros")
@CrossOrigin(origins = "*")
public class BarbeiroController {
	@Autowired
    private BarbeiroService service;

    @PostMapping("/cadastro")
    public ResponseEntity<BarbeiroRespondeDTO> cadastrar(@Valid @RequestBody CadastroBarbeiroDTO dto) {
        BarbeiroRespondeDTO barbeiro = service.cadastrar(dto);
        return ResponseEntity.status(201).body(barbeiro);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginBarbeiroDTO dto) {
        return ResponseEntity.ok(service.login(dto));
    }
    
    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> esqueciSenha(@RequestBody ResetSenhaDTO dto) {
        service.solicitarResetSenha(dto.getEmail());
        return ResponseEntity.ok("Email enviado");
    }
    
    @PostMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(
            @Valid @RequestBody RedefinirSenhaDTO dto) {

        service.redefinirSenha(dto.getToken(), dto.getNovaSenha());
        return ResponseEntity.ok("Senha redefinida com sucesso");
    }
    
}
