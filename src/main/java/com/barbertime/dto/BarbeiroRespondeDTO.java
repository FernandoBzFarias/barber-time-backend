package com.barbertime.dto;

public class BarbeiroRespondeDTO {
	  private Long id;
	  private String nome;
	  private String email;
	  private String telefone;

	  public BarbeiroRespondeDTO() {}
	  public BarbeiroRespondeDTO(Long id, String nome, String email, String telefone) {
	       this.id = id;
	       this.nome = nome;
	       this.email = email;
	       this.telefone = telefone;
	    }

	   public Long getId() { return id; }
	   public void setId(Long id) { this.id = id; }

	   public String getNome() { return nome; }
	   public void setNome(String nome) { this.nome = nome; }

	   public String getEmail() { return email; }
	   public void setEmail(String email) { this.email = email; }

	   public String getTelefone() { return telefone; }
	   public void setTelefone(String telefone) { this.telefone = telefone; }
}
