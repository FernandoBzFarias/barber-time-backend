package com.barbertime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.barbertime.dto.CadastroBarbeiroDTO;
import com.barbertime.dto.LoginBarbeiroDTO;
import com.barbertime.entity.Barbeiro;
import com.barbertime.repository.BarbeiroRepository;

@Service
public class BarbeiroService {
	  @Autowired
	  private BarbeiroRepository repository;

	    @Autowired
	    private BCryptPasswordEncoder passwordEncoder;

	    // Cadastro
	    public Barbeiro cadastrar(CadastroBarbeiroDTO dto) {

	        if (repository.findByEmail(dto.getEmail()).isPresent()) {
	            throw new RuntimeException("Email já cadastrado");
	        }

	        Barbeiro barbeiro = new Barbeiro();
	        barbeiro.setNome(dto.getNome());
	        barbeiro.setEmail(dto.getEmail());
	        barbeiro.setTelefone(dto.getTelefone());

	        // criptografando senha
	        barbeiro.setSenha(passwordEncoder.encode(dto.getSenha()));

	        return repository.save(barbeiro);
	    }

	    // Login
	    public Barbeiro login(LoginBarbeiroDTO dto) {

	        Barbeiro barbeiro = repository.findByEmail(dto.getEmail())
	                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

	        if (!passwordEncoder.matches(dto.getSenha(), barbeiro.getSenha())) {
	            throw new RuntimeException("Email ou senha inválidos");
	        }

	        return barbeiro;
	    }
}
