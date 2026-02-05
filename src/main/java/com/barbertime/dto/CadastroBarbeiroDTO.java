package com.barbertime.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CadastroBarbeiroDTO {
	 @NotBlank(message = "Nome é obrigatório")
	 private String nome;
	 @Email(message = "Email inválido")
	 @NotBlank(message = "Email é obrigatório")
	 private String email;
	 @NotBlank(message = "Senha é obrigatória")
	 @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
	 private String senha;
	 @NotBlank(message = "Telefone é obrigatório")
	 private String telefone;
	 
	 public String getNome() {
	 return nome; }

	 public void setNome(String nome) {
	 this.nome = nome;}

	 public String getEmail() {
	 return email;}
	    
	 public void setEmail(String email) {
	 this.email = email;}

	 public String getSenha() {
	 return senha;}
	    
	 public void setSenha(String senha) {
     this.senha = senha;}

	 public String getTelefone() {
	 return telefone;}
	    
	 public void setTelefone(String telefone) {
	 this.telefone = telefone;}
}
