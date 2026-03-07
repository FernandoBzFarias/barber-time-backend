package com.barbertime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.barbertime.dto.BarbeariaRequestDTO;
import com.barbertime.entity.Barbearia;
import com.barbertime.service.BarbeariaService;
import com.barbertime.service.FileStorageService;

@RestController
@RequestMapping("/api/barbearia")
public class BarbeariaController {

    @Autowired
    private BarbeariaService service;

    @Autowired
    private FileStorageService fileStorageService; 

    @PutMapping("/{id}/configuracoes")
    public ResponseEntity<Barbearia> atualizar(
            @PathVariable Long id,
            @RequestBody BarbeariaRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Passamos o e-mail do barbeiro logado (pegue do Token JWT)
        Barbearia atualizada = service.atualizarDados(id, dto, userDetails.getUsername());
        return ResponseEntity.ok(atualizada);
    }

    // Rota separada para a Foto/Slogan
    @PostMapping("/{id}/logo")
    public ResponseEntity<String> uploadLogo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        
        String urlImagem = fileStorageService.save(file);
        service.atualizarLogo(id, urlImagem);
        
        return ResponseEntity.ok("Logo atualizada com sucesso: " + urlImagem);
    }
}
