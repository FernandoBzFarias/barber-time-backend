package com.barbertime.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name= "barbeiros")
public class Barbeiro {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String telefone;
    
    @Column(nullable = false)
    private Long barbeariaId;

	public Long getBarbeariaId() {
		return barbeariaId;
	}

	public void setBarbeariaId(Long barbeariaId) {
		this.barbeariaId = barbeariaId;
	}

	public Barbeiro() {}

    // Getters e Setters
    public Long getId() {
        return id;}

    public String getNome() {
        return nome;}

    public void setNome(String nome) {
        this.nome = nome;}

    public void setId(Long id) {
        this.id = id;}

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
