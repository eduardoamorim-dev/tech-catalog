package com.web_ii.tech_catalog.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
public class ItemPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_pedido", nullable = false)
    private Long idPedido;
    
    @Column(name = "id_produto", nullable = false)
    private Long idProduto;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    @Column(nullable = false)
    private Integer quantidade;
    
    @NotNull(message = "Valor unitário é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor unitário deve ser maior que zero")
    @Column(name = "valor_unitario", nullable = false)
    private BigDecimal  valorUnitario;

    
    @NotNull(message = "Valor total é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor total deve ser maior que zero")
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;
    
    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", insertable = false, updatable = false)
    private Pedido pedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", insertable = false, updatable = false)
    private TechCatalog produto;
    
    // Construtores
    public ItemPedido() {}
    
    // public ItemPedido(Long idPedido, Long idProduto, Integer quantidade, BigDecimal valorUnitario) {
    //     this.idPedido = idPedido;
    //     this.idProduto = idProduto;
    //     this.quantidade = quantidade;
    //     this.valorUnitario = valorUnitario;
    //     this.calcularValorTotal();
    // }

    public ItemPedido(Long idPedido, Long idProduto, Integer quantidade, BigDecimal valorUnitario) {
        this.idPedido = idPedido;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
         this.calcularValorTotal();
    }
    
    // Método para calcular valor total automaticamente
    public void calcularValorTotal() {
        if (this.quantidade != null && this.valorUnitario != null) {
            BigDecimal quantidadeBD = BigDecimal.valueOf(this.quantidade);
            this.valorTotal = this.valorUnitario.multiply(quantidadeBD);
        }
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getIdPedido() {
        return idPedido;
    }
    
    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
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
        this.calcularValorTotal();
    }
    
    public BigDecimal  getValorUnitario() {
        return valorUnitario;
    }
    
    public void setValorUnitario(BigDecimal  valorUnitario) {
        this.valorUnitario = valorUnitario;
        this.calcularValorTotal();
    }
    
    public BigDecimal getValorTotal() {
        return valorTotal;
    }
    
    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
    
    public Pedido getPedido() {
        return pedido;
    }
    
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    
    public TechCatalog getProduto() {
        return produto;
    }
    
    public void setProduto(TechCatalog produto) {
        this.produto = produto;
    }
    
    @Override
    public String toString() {
        return "ItemPedido{" +
                "id=" + id +
                ", idPedido=" + idPedido +
                ", idProduto=" + idProduto +
                ", quantidade=" + quantidade +
                ", valorUnitario=" + valorUnitario +
                ", valorTotal=" + valorTotal +
                '}';
    }
}