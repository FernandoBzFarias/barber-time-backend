package com.barbertime.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String telefone; // O telefone será o ID "natural" para busca

    @Column(columnDefinition = "TEXT")
    private String notasTecnicas;
    
    @Column(nullable = false)
    private Long barbeariaId; 
    
    public Long getBarbeariaId() {
		return barbeariaId;
	}

	public void setBarbeariaId(Long barbeariaId) {
		this.barbeariaId = barbeariaId;
	}

	public Integer getCortesSeguidosSemAtraso() {
		return cortesSeguidosSemAtraso;
	}

	public void setCortesSeguidosSemAtraso(Integer cortesSeguidosSemAtraso) {
		this.cortesSeguidosSemAtraso = cortesSeguidosSemAtraso;
	}

	@Column(nullable = false)
    private Integer cortesSeguidosSemAtraso = 0; 

    private Integer totalCortes = 0;
    private Integer quantidadeAtrasos = 0;
    private Double totalGasto = 0.0;

    @Enumerated(EnumType.STRING)
    private ModalidadeCliente modalidade = ModalidadeCliente.NOVO;

    public enum ModalidadeCliente { NOVO, FIEL, VIP, ATRASADO }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getNotasTecnicas() {
		return notasTecnicas;
	}

	public void setNotasTecnicas(String notasTecnicas) {
		this.notasTecnicas = notasTecnicas;
	}

	public Integer getTotalCortes() {
		return totalCortes;
	}

	public void setTotalCortes(Integer totalCortes) {
		this.totalCortes = totalCortes;
	}

	public Integer getQuantidadeAtrasos() {
		return quantidadeAtrasos;
	}

	public void setQuantidadeAtrasos(Integer quantidadeAtrasos) {
		this.quantidadeAtrasos = quantidadeAtrasos;
	}

	public Double getTotalGasto() {
		return totalGasto;
	}

	public void setTotalGasto(Double totalGasto) {
		this.totalGasto = totalGasto;
	}

	public ModalidadeCliente getModalidade() {
		return modalidade;
	}

	public void setModalidade(ModalidadeCliente modalidade) {
		this.modalidade = modalidade;
	}
    
    
    
}
