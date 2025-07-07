package com.web_ii.tech_catalog.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_user", nullable = false)
    private Long idUser;
    
    @NotNull(message = "Valor total é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor total deve ser maior que zero")
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;
    
    @NotNull(message = "Status é obrigatório")
    @Column(name = "status", nullable = false)
    private String status; // PENDENTE, CONFIRMADO, ENVIADO, ENTREGUE, CANCELADO
    
    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;
    
    @Column(name = "endereco_entrega", nullable = false)
    private String enderecoEntrega;
    
    @Column(name = "forma_pagamento", nullable = false)
    private String formaPagamento; // CARTAO, PIX, BOLETO
    
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;
    
    // Relacionamento com usuário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", insertable = false, updatable = false)
    private User user;
    
    // Relacionamento com itens do pedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemPedido> itens;
    
    // Construtores
    public Pedido() {
        this.dataPedido = LocalDateTime.now();
        this.status = "PENDENTE";
    }
    
    public Pedido(Long idUser, BigDecimal valorTotal, String enderecoEntrega, String formaPagamento) {
        this();
        this.idUser = idUser;
        this.valorTotal = valorTotal;
        this.enderecoEntrega = enderecoEntrega;
        this.formaPagamento = formaPagamento;
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
    
    public BigDecimal getValorTotal() {
        return valorTotal;
    }
    
    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getDataPedido() {
        return dataPedido;
    }
    
    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }
    
    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }
    
    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }
    
    public String getFormaPagamento() {
        return formaPagamento;
    }
    
    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public List<ItemPedido> getItens() {
        return itens;
    }
    
    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }
    
    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", idUser=" + idUser +
                ", valorTotal=" + valorTotal +
                ", status='" + status + '\'' +
                ", dataPedido=" + dataPedido +
                ", enderecoEntrega='" + enderecoEntrega + '\'' +
                ", formaPagamento='" + formaPagamento + '\'' +
                '}';
    }
}