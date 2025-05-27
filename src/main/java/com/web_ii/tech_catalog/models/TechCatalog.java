package com.web_ii.tech_catalog.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "eletronicos")
public class TechCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false)
    private String codigo;
    
    @NotBlank(message = "Título não pode ser vazio")
    @Column(name = "titulo", nullable = false)
    private String titulo;
    
    @NotNull(message = "Valor base não pode ser nulo")
    @Column(name = "valor_base")
    private Double valorBase;

    @Column(name = "especificacao_tecnica", columnDefinition = "TEXT")
    private String especificacaoTecnica;

    @NotNull(message = "Categoria não pode ser nula")
    @Column(name = "categoria")
    private String categoria;

    @Column(name = "fabricante")
    private String fabricante;

    @Column(name = "nivel_estoque")
    private String nivelEstoque;

    @Column(name = "estado")
    private String estado;

    @Column(name = "prioridade")
    private String prioridade;

    @Column(name = "recursos_adicionais")
    private String recursosAdicionais;

    @Column(name = "locais_disponiveis")
    private String locaisDisponiveis;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getValorBase() {
        return valorBase;
    }

    public void setValorBase(Double valorBase) {
        this.valorBase = valorBase;
    }

    public String getEspecificacaoTecnica() {
        return especificacaoTecnica;
    }

    public void setEspecificacaoTecnica(String especificacaoTecnica) {
        this.especificacaoTecnica = especificacaoTecnica;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getNivelEstoque() {
        return nivelEstoque;
    }

    public void setNivelEstoque(String nivelEstoque) {
        this.nivelEstoque = nivelEstoque;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getRecursosAdicionais() {
        return recursosAdicionais;
    }

    public void setRecursosAdicionais(String recursosAdicionais) {
        this.recursosAdicionais = recursosAdicionais;
    }

    public String getLocaisDisponiveis() {
        return locaisDisponiveis;
    }

    public void setLocaisDisponiveis(String locaisDisponiveis) {
        this.locaisDisponiveis = locaisDisponiveis;
    }
}