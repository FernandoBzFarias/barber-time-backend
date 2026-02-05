package com.barbertime.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthTestController {
	@GetMapping("/api/teste")
    public String teste() {
        return "Rota protegida funcionando!";
    }
}
