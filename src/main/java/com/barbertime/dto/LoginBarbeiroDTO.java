package com.barbertime.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginBarbeiroDTO {
	@NotBlank(message = "Email é obrigatório")
	private String email;
	@NotBlank(message = "Senha é obrigatória")
    private String senha;

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
}
