package com.barbertime.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.barbertime.dto.BarbeiroRespondeDTO;
import com.barbertime.dto.CadastroBarbeiroDTO;
import com.barbertime.dto.HorarioDisponivelDTO;
import com.barbertime.dto.LoginBarbeiroDTO;
import com.barbertime.dto.NovoAgendamentoDTO;
import com.barbertime.dto.RedefinirSenhaDTO;
import com.barbertime.dto.ResetSenhaDTO;
import com.barbertime.service.AgendamentoService;
import com.barbertime.service.BarbeiroService;

@RestController
@RequestMapping("/api/barbeiros")
@CrossOrigin(origins = "*")
public class BarbeiroController {
	@Autowired
    private BarbeiroService service;
	
	@Autowired
    private AgendamentoService agendamentoService;
	  
    @PostMapping("/cadastro")
    public ResponseEntity<BarbeiroRespondeDTO> cadastrar(@Valid @RequestBody CadastroBarbeiroDTO dto) {
        BarbeiroRespondeDTO barbeiro = service.cadastrar(dto);
        return ResponseEntity.status(201).body(barbeiro);}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginBarbeiroDTO dto) {
        return ResponseEntity.ok(service.login(dto));}
    
    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> esqueciSenha(@RequestBody ResetSenhaDTO dto) {
        service.solicitarResetSenha(dto.getEmail());
        return ResponseEntity.ok("Email enviado");}
    
    @PostMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(
            @Valid @RequestBody RedefinirSenhaDTO dto) {
        service.redefinirSenha(dto.getToken(), dto.getNovaSenha());
        return ResponseEntity.ok("Senha redefinida com sucesso");} 
    
    @PostMapping
    public ResponseEntity<String> agendar(@RequestBody NovoAgendamentoDTO dto) {
        String mensagem = agendamentoService.criarAgendamento(dto);
           return ResponseEntity.ok(mensagem);
        }
    
    @GetMapping("/disponibilidade")
    public ResponseEntity<List<HorarioDisponivelDTO>> consultar(
            @RequestParam Long barbeiroId, 
            @RequestParam LocalDate data) {
        return ResponseEntity.ok(agendamentoService.listarDisponibilidade(barbeiroId, data));
    }
    
    @PostMapping("/configurar-grade")
    public ResponseEntity<String> configurarGrade(@RequestBody List<LocalTime> horarios) {
        agendamentoService.salvarGrade(horarios);
        return ResponseEntity.ok("Sua grade de horários foi atualizada com sucesso!");
    }
}
