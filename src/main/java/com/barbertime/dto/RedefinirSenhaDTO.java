package com.barbertime.dto;

import jakarta.validation.constraints.NotBlank;

public class RedefinirSenhaDTO {
	   @NotBlank
	    private String token;
	    @NotBlank
	    private String novaSenha;

	    public String getToken() {
	        return token; }

	    public void setToken(String token) {
	        this.token = token;}

	    public String getNovaSenha() {
	        return novaSenha;}

	    public void setNovaSenha(String novaSenha) {
	        this.novaSenha = novaSenha;}
}
