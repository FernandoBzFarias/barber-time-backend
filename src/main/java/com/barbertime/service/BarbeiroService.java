package com.barbertime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.barbertime.dto.BarbeiroRespondeDTO;
import com.barbertime.dto.CadastroBarbeiroDTO;
import com.barbertime.dto.LoginBarbeiroDTO;
import com.barbertime.dto.LoginResponseDTO;
import com.barbertime.entity.Barbeiro;
import com.barbertime.repository.BarbeiroRepository;
import com.barbertime.security.JwtService;

@Service
public class BarbeiroService {
	  @Autowired
	  private BarbeiroRepository repository;
	  @Autowired
	  private BCryptPasswordEncoder passwordEncoder; 
	  @Autowired
	  private JwtService jwtService;
	  
	    // Cadastro
	    public BarbeiroRespondeDTO cadastrar(CadastroBarbeiroDTO dto) {

	        if (repository.findByEmail(dto.getEmail()).isPresent()) {
	            throw new RuntimeException("Email já cadastrado");
	        }

	        Barbeiro barbeiro = new Barbeiro();
	        barbeiro.setNome(dto.getNome());
	        barbeiro.setEmail(dto.getEmail());
	        barbeiro.setTelefone(dto.getTelefone());
	        barbeiro.setSenha(passwordEncoder.encode(dto.getSenha()));

	        Barbeiro salvo = repository.save(barbeiro);

	        return new BarbeiroRespondeDTO(
	                salvo.getId(),
	                salvo.getNome(),
	                salvo.getEmail(),
	                salvo.getTelefone()
	        );
	    }
	    // Login 
	    public LoginResponseDTO login(LoginBarbeiroDTO dto) {
	        Barbeiro barbeiro = repository.findByEmail(dto.getEmail())
	                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

	        if (!passwordEncoder.matches(dto.getSenha(), barbeiro.getSenha())) {
	            throw new RuntimeException("Email ou senha inválidos");
	        }

	        String token = jwtService.generateToken(barbeiro.getEmail());

	        return new LoginResponseDTO(token);
	    }
}
