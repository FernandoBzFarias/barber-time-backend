package com.barbertime.service;


import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.barbertime.dto.BarbeiroConfigDTO;
import com.barbertime.dto.BarbeiroRespondeDTO;
import com.barbertime.dto.CadastroBarbeiroDTO;
import com.barbertime.dto.LoginBarbeiroDTO;
import com.barbertime.dto.LoginResponseDTO;
import com.barbertime.dto.MarketingDTO;
import com.barbertime.entity.Barbeiro;
import com.barbertime.entity.PasswordResetToken;
import com.barbertime.repository.BarbeiroRepository;
import com.barbertime.repository.PasswordResetTokenRepository;
import com.barbertime.security.JwtService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;



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
	    
	    // Redefinir a senha 
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
	    
	    @Transactional
	    public void atualizarPerfil(String emailLogado, BarbeiroConfigDTO dto) {
	        Barbeiro barbeiro = repository.findByEmail(emailLogado)
	                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado."));
	        

	        // Atualiza dados básicos
	        barbeiro.setNome(dto.nome());
	        barbeiro.setTelefone(dto.telefone());
	        
	        // Validação de E-mail
	        if (!barbeiro.getEmail().equals(dto.email())) {
	            if (repository.findByEmail(dto.email()).isPresent()) {
	                throw new RuntimeException("Este e-mail já está em uso.");
	            }
	            barbeiro.setEmail(dto.email());
	        }

	        // Atualização de Senha
	        if (dto.novaSenha() != null && !dto.novaSenha().isBlank()) {
	            barbeiro.setSenha(passwordEncoder.encode(dto.novaSenha()));
	        }

	        // Persistência das Preferências de Notificação
	        barbeiro.setNotificarAgendamentos(dto.notificarAgendamentos());
	        barbeiro.setNotificarLembretes(dto.notificarLembretes());
	        barbeiro.setNotificarCancelamentos(dto.notificarCancelamentos());

	        repository.save(barbeiro);
	    }
	    	    
	    @Transactional(readOnly = true)
	    public MarketingDTO obterDadosMarketing() {
	        String email = SecurityContextHolder.getContext().getAuthentication().getName();
	        Barbeiro barbeiro = repository.findByEmail(email)
	                .orElseThrow(() -> new EntityNotFoundException("Barbeiro não encontrado"));

	        // Em um sistema real, você pegaria a URL base de um arquivo de configuração (.properties)
	        String baseUrl = "https://barbertime.com/"; 
	        String linkCompleto = baseUrl + barbeiro.getSlug();

	        return new MarketingDTO(linkCompleto, barbeiro.getSlug(), barbeiro.getNome());
	    } 	    	    
}
