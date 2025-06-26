package com.web_ii.tech_catalog.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "carrinho")
public class Carrinho {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_user", nullable = false)
    private Long idUser;
    
    @Column(name = "id_produto", nullable = false)
    private Long idProduto;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    @Column(nullable = false)
    private Integer quantidade;
    
    @NotNull(message = "Valor do produto é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor do produto deve ser maior que zero")
    private Double valorProduto;
    
    @NotNull(message = "Valor total é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor total deve ser maior que zero")
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;
    
    @Column(name = "data_adicao", nullable = false)
    private LocalDateTime dataAdicao;
    
    // Relacionamentos para facilitar consultas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", insertable = false, updatable = false)
    private TechCatalog produto;
    
    // Construtores
    public Carrinho() {
        this.dataAdicao = LocalDateTime.now();
    }
    
    public Carrinho(Long idUser, Long idProduto, Integer quantidade, Double valorProduto) {
        this();
        this.idUser = idUser;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.valorProduto = valorProduto;
        this.calcularValorTotal();
    }
    
    // Método para calcular valor total automaticamente
    public void calcularValorTotal() {
        if (this.quantidade != null && this.valorProduto != null) {
            BigDecimal valorProdutoBD = BigDecimal.valueOf(this.valorProduto);
            BigDecimal quantidadeBD = BigDecimal.valueOf(this.quantidade);
            this.valorTotal = valorProdutoBD.multiply(quantidadeBD);
        }
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getIdUser() {
        return idUser;
    }
    
    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }
    
    public Long getIdProduto() {
        return idProduto;
    }
    
    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }
    
    public Integer getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        this.calcularValorTotal(); // Recalcula o valor total quando a quantidade muda
    }
    
    public Double getValorProduto() {
        return valorProduto;
    }
    
    public void setValorProduto(Double valorProduto) {
        this.valorProduto = valorProduto;
        this.calcularValorTotal(); // Recalcula o valor total quando o valor do produto muda
    }
    
    public BigDecimal getValorTotal() {
        return valorTotal;
    }
    
    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
    
    public LocalDateTime getDataAdicao() {
        return dataAdicao;
    }
    
    public void setDataAdicao(LocalDateTime dataAdicao) {
        this.dataAdicao = dataAdicao;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public TechCatalog getProduto() {
        return produto;
    }
    
    public void setProduto(TechCatalog produto) {
        this.produto = produto;
    }
    
    @Override
    public String toString() {
        return "Carrinho{" +
                "id=" + id +
                ", idUser=" + idUser +
                ", idProduto=" + idProduto +
                ", quantidade=" + quantidade +
                ", valorProduto=" + valorProduto +
                ", valorTotal=" + valorTotal +
                ", dataAdicao=" + dataAdicao +
                '}';
    }
}