package com.barbertime.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.barbertime.dto.AgendaBarbeiroResponseDTO;
import com.barbertime.dto.AgendaGeralBarbeariaDTO;
import com.barbertime.dto.BarbeiroRespondeDTO;
import com.barbertime.dto.CadastroBarbeiroDTO;
import com.barbertime.dto.HorarioDisponivelDTO;
import com.barbertime.dto.LoginBarbeiroDTO;
import com.barbertime.dto.NovoAgendamentoDTO;
import com.barbertime.dto.RedefinirSenhaDTO;
import com.barbertime.dto.ResetSenhaDTO;
import com.barbertime.dto.ResumoDiarioDTO;
import com.barbertime.entity.Servico;
import com.barbertime.entity.StatusAgendamento;
import com.barbertime.service.AgendamentoService;
import com.barbertime.service.BarbeiroService;

@RestController
@RequestMapping("/api/barbeiros")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BarbeiroController {
	@Autowired
    private BarbeiroService service;
	
	@Autowired
    private AgendamentoService agendamentoService;
	
	@Autowired
    private com.barbertime.service.ServicoService servicoService;
	
	@Autowired
    private com.barbertime.repository.BarbeiroRepository barbeiroRepository;
	
	@GetMapping("/publico/servicos/{barbeariaId}")
    public ResponseEntity<List<Servico>> listarServicosPublicos(@PathVariable Long barbeariaId) {
        return ResponseEntity.ok(servicoService.listarPorBarbearia(barbeariaId));
    }
	  
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
    
    // Rota de disponibilidade (Já é pública no seu SecurityConfig)
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
    
    @GetMapping("/agenda-dashboard")
    public ResponseEntity<List<AgendaBarbeiroResponseDTO>> verAgenda(
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate data,
            @RequestParam(required = false) Long barbeiroId) { 
        
        // Se o barbeiroId não for enviado, o Service entende que é "Todos"
        List<AgendaBarbeiroResponseDTO> agenda = agendamentoService.buscarAgendaDashboard(data, barbeiroId);
        return ResponseEntity.ok(agenda);
    }

    @PatchMapping("/agenda/{id}/status")
    public ResponseEntity<Void> mudarStatus(
            @PathVariable Long id, 
            @RequestParam StatusAgendamento novoStatus) {
        
        agendamentoService.atualizarStatus(id, novoStatus);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/agenda-geral")
    public ResponseEntity<List<AgendaGeralBarbeariaDTO>> verAgendaGeral(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        
        // Retorna os dados agrupados por Barbeiro (Ideal para colunas no Front)
        List<AgendaGeralBarbeariaDTO> agendaGeral = agendamentoService.buscarAgendaCompletaDaBarbearia(data);
        return ResponseEntity.ok(agendaGeral);
    }
    
    @GetMapping("/resumo-dia")
    public ResponseEntity<ResumoDiarioDTO> verResumoDiario(
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate data) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        com.barbertime.entity.Barbeiro logado = barbeiroRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));

        return ResponseEntity.ok(agendamentoService.obterResumoDiario(logado.getId(), data));
    }
}
