package com.barbertime.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TokenRecuperacao {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	 private String token;
	 private String email;
	 private LocalDateTime expiracao;
	 public Long getId() {
		 return id;
	 }
	 public void setId(Long id) {
		 this.id = id;
	 }
	 public String getToken() {
		 return token;
	 }
	 public void setToken(String token) {
		 this.token = token;
	 }
	 public String getEmail() {
		 return email;
	 }
	 public void setEmail(String email) {
		 this.email = email;
	 }
	 public LocalDateTime getExpiracao() {
		 return expiracao;
	 }
	 public void setExpiracao(LocalDateTime expiracao) {
		 this.expiracao = expiracao;
	 }
	 
}
