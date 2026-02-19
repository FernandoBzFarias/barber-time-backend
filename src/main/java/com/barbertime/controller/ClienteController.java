package com.barbertime.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.barbertime.dto.ClienteDetalheDTO;
import com.barbertime.dto.ClienteResumoDTO;
import com.barbertime.dto.NovoClienteDTO;
import com.barbertime.repository.BarbeiroRepository;
import com.barbertime.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @Autowired
    private BarbeiroRepository barbeiroRepository;

    // Método auxiliar para pegar a barbearia do barbeiro logado via SecurityContext
    private Long getBarbeariaIdLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return barbeiroRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Barbeiro logado não encontrado"))
                .getBarbeariaId();
    }

    @GetMapping
    public ResponseEntity<List<ClienteResumoDTO>> listar(@RequestParam(required = false) String busca) {
        // Agora passamos o barbeariaId para o service
        return ResponseEntity.ok(service.listarClientes(busca, getBarbeariaIdLogado()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDetalheDTO> detalhes(@PathVariable Long id) {
        // Agora passamos o barbeariaId para o service
        return ResponseEntity.ok(service.obterDetalhes(id, getBarbeariaIdLogado()));
    }

    @PatchMapping("/{id}/notas")
    public ResponseEntity<Void> atualizarNotas(@PathVariable Long id, @RequestBody Map<String, String> body) {
        // Agora passamos o barbeariaId para o service
        service.atualizarNotas(id, body.get("notas"), getBarbeariaIdLogado());
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping
    public ResponseEntity<ClienteResumoDTO> cadastrar(@RequestBody NovoClienteDTO dto) {
        // getBarbeariaIdLogado() é aquele método auxiliar que já temos no seu Controller
        ClienteResumoDTO novoCliente = service.cadastrarClienteManual(dto, getBarbeariaIdLogado());
        return ResponseEntity.ok(novoCliente);
    }
}