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

    @NotBlank(message = "Título não pode ser vazio")
    @Column(name = "titulo", nullable = false)
    private String titulo;
    
    @NotNull(message = "Valor base não pode ser nulo")
    @Column(name = "valor_base")
    private Double valorBase;

    @NotNull(message = "Descricao não pode ser nula")
    @Column(name = "descricao_produto", columnDefinition = "TEXT")
    private String descricao_produto;

    @Column(name = "especificacao_tecnica", columnDefinition = "TEXT")
    private String especificacaoTecnica;

    @NotNull(message = "Categoria não pode ser nula")
    @Column(name = "categoria")
    private String categoria;

    @NotNull(message = "Nivel de estoque não pode ser nulo")
    @Column(name = "nivel_estoque")
    private Integer nivelEstoque;

    @Column(name = "recursos_adicionais")
    private String recursosAdicionais;

    @Column(name = "locais_disponiveis")
    private String locaisDisponiveis;

    @NotNull(message = "Imagem não pode ser nula")
    @Column(name = "imagem_url")
    private String imagemUrl;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescricaoProduto() {
        return descricao_produto;
    }

    public void setDescricaoProduto(String descricao_produto) {
        this.descricao_produto = descricao_produto;
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

    public Integer getNivelEstoque() {
        return nivelEstoque;
    }

    public void setNivelEstoque(Integer nivelEstoque) {
        this.nivelEstoque = nivelEstoque;
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

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }
}