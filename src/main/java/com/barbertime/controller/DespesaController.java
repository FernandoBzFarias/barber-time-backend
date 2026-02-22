package com.barbertime.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barbertime.entity.Barbeiro;
import com.barbertime.entity.Despesa;
import com.barbertime.repository.BarbeiroRepository;
import com.barbertime.service.DespesaService;

@RestController
@RequestMapping("/api/despesas")
public class DespesaController {

    @Autowired
    private DespesaService despesaService;
    @Autowired
    private BarbeiroRepository barbeiroRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listar(@RequestParam int mes, @RequestParam int ano) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Barbeiro chefe = barbeiroRepository.findByEmail(email).orElseThrow();

        List<Despesa> lista = despesaService.listarPorMes(chefe.getBarbeariaId(), mes, ano);
        Double total = despesaService.calcularTotalMes(chefe.getBarbeariaId(), mes, ano);

        return ResponseEntity.ok(Map.of(
            "itens", lista,
            "totalGeral", total
        ));
    }

    @PostMapping
    public ResponseEntity<Despesa> adicionar(@RequestBody Despesa despesa) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Barbeiro chefe = barbeiroRepository.findByEmail(email).orElseThrow();
        
        despesa.setBarbeariaId(chefe.getBarbeariaId());
        return ResponseEntity.ok(despesaService.salvar(despesa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        despesaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
