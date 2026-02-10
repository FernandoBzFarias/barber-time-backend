package com.barbertime.service;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.barbertime.dto.BarbeiroRespondeDTO;
import com.barbertime.dto.CadastroBarbeiroDTO;
import com.barbertime.dto.LoginBarbeiroDTO;
import com.barbertime.dto.LoginResponseDTO;
import com.barbertime.entity.Barbeiro;
import com.barbertime.entity.PasswordResetToken;
import com.barbertime.repository.BarbeiroRepository;
import com.barbertime.repository.PasswordResetTokenRepository;
import com.barbertime.security.JwtService;


@Service
public class BarbeiroService {
	  @Autowired
	  private BarbeiroRepository repository;
	  @Autowired
	  private BCryptPasswordEncoder passwordEncoder1; 
	  @Autowired
	  private JwtService jwtService;

	  @Autowired
	  private PasswordResetTokenRepository tokenRepository;
	  @Autowired
	  private EmailService emailService;
	  @Autowired
	  private BCryptPasswordEncoder passwordEncoder;
	    // Cadastro
	    public BarbeiroRespondeDTO cadastrar(CadastroBarbeiroDTO dto) {

	        if (repository.findByEmail(dto.getEmail()).isPresent()) {
	            throw new RuntimeException("Email já cadastrado");
	        }

	        Barbeiro barbeiro = new Barbeiro();
	        barbeiro.setNome(dto.getNome());
	        barbeiro.setEmail(dto.getEmail());
	        barbeiro.setTelefone(dto.getTelefone());
	        barbeiro.setSenha(passwordEncoder1.encode(dto.getSenha()));

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

	        if (!passwordEncoder1.matches(dto.getSenha(), barbeiro.getSenha())) {
	            throw new RuntimeException("Email ou senha inválidos");
	        }

	        String token = jwtService.generateToken(barbeiro.getEmail());

	        return new LoginResponseDTO(token);
	    }
	    
	    // Recuperar Senha 
	    public void solicitarResetSenha(String email) {
	        Barbeiro barbeiro = repository.findByEmail(email)
	                .orElseThrow(() -> new RuntimeException("Email não encontrado"));
	        String token = java.util.UUID.randomUUID().toString();
	        PasswordResetToken resetToken = new PasswordResetToken(
	                token,
	                barbeiro.getEmail(),
	                java.time.LocalDateTime.now().plusMinutes(30));
	        tokenRepository.save(resetToken);
	        emailService.enviarEmailRecuperacao(email, token);
	    }	 
	    public void redefinirSenha(String token, String novaSenha) {

	        PasswordResetToken resetToken = tokenRepository.findByToken(token)
	            .orElseThrow(() -> new RuntimeException("Token inválido"));

	        if (resetToken.getExpiration().isBefore(LocalDateTime.now())) {
	            throw new RuntimeException("Token expirado");
	        }

	        Barbeiro barbeiro = repository.findByEmail(resetToken.getEmail())
	            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

	        barbeiro.setSenha(passwordEncoder.encode(novaSenha));
	        repository.save(barbeiro);

	        // Segurança: remove o token após uso
	        tokenRepository.delete(resetToken);
	    }
}
