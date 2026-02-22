package com.barbertime.entity;

import java.util.List;
import jakarta.persistence.*;


@Entity
public class Servico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;        // Ex: Corte Degradê
    private Double preco;      // Ex: 45.00
    private Integer duracao;    // Em minutos: 30
    private Boolean ativo;      // Para mostrar ou não no agendamento
    
    @ManyToMany
    @JoinTable(
        name = "servico_barbeiro",
        joinColumns = @JoinColumn(name = "servico_id"),
        inverseJoinColumns = @JoinColumn(name = "barbeiro_id")
    )
    private List<Barbeiro> barbeirosQueFazem;
    
    private Long barbeariaId;

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

	public Double getPreco() {
		return preco;
	}

	public void setPreco(Double preco) {
		this.preco = preco;
	}

	public Integer getDuracao() {
		return duracao;
	}

	public void setDuracao(Integer duracao) {
		this.duracao = duracao;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public List<Barbeiro> getBarbeirosQueFazem() {
		return barbeirosQueFazem;
	}

	public void setBarbeirosQueFazem(List<Barbeiro> barbeirosQueFazem) {
		this.barbeirosQueFazem = barbeirosQueFazem;
	}

	public Long getBarbeariaId() {
		return barbeariaId;
	}

	public void setBarbeariaId(Long barbeariaId) {
		this.barbeariaId = barbeariaId;
	}
    
    
    
}
