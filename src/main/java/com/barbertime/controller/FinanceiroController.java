package com.barbertime.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.barbertime.dto.FinanceiroResumoDTO;
import com.barbertime.entity.Barbeiro;
import com.barbertime.entity.Role;
import com.barbertime.repository.BarbeiroRepository;
import com.barbertime.service.FinanceiroService;


@RestController
@RequestMapping("/api/financeiro")
public class FinanceiroController {

    @Autowired
    private FinanceiroService financeiroService;
    @Autowired
    private BarbeiroRepository barbeiroRepository;

    @GetMapping("/resumo")
    public ResponseEntity<FinanceiroResumoDTO> getResumo(@RequestParam int mes, @RequestParam int ano) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Barbeiro logado = barbeiroRepository.findByEmail(email).get();

        // VALIDAÇÃO DE SEGURANÇA: Só o ADMIN entra aqui
        if (logado.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(financeiroService.calcularFechamentoMensal(logado.getBarbeariaId(), mes, ano));
    }

    @PatchMapping("/comissao/{id}")
    public ResponseEntity<Void> mudarComissao(@PathVariable Long id, @RequestBody Double novoPercentual) {
        financeiroService.atualizarComissao(id, novoPercentual);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/validar-acesso")
    public ResponseEntity<Map<String, Boolean>> validarAcesso(@RequestBody Map<String, String> payload) {
        String senhaDigitada = payload.get("senha");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Barbeiro chefe = barbeiroRepository.findByEmail(email).orElseThrow();

        // Aqui usamos o Equals, mas o ideal é usar o BCrypt se a senha for criptografada
        boolean autorizado = chefe.getSenhaFinanceira().equals(senhaDigitada);
        
        return ResponseEntity.ok(Map.of("autorizado", autorizado));
    }
}
