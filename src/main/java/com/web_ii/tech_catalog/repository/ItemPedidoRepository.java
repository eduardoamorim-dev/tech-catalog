package com.web_ii.tech_catalog.repository;

import com.web_ii.tech_catalog.models.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    
    /**
     * Busca itens de um pedido específico
     */
    List<ItemPedido> findByIdPedido(Long pedidoId);
    
    /**
     * Busca itens por produto
     */
    List<ItemPedido> findByIdProduto(Long produtoId);
    
    /**
     * Busca itens por pedido e produto
     */
    List<ItemPedido> findByIdPedidoAndIdProduto(Long pedidoId, Long produtoId);
    
    /**
     * Remove todos os itens de um pedido usando query personalizada
     * para evitar problemas com cascade
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ItemPedido ip WHERE ip.idPedido = :pedidoId")
    void deleteByIdPedido(@Param("pedidoId") Long pedidoId);
    
    /**
     * Remove itens por produto
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ItemPedido ip WHERE ip.idProduto = :produtoId")
    void deleteByIdProduto(@Param("produtoId") Long produtoId);
    
    /**
     * Conta itens de um pedido
     */
    long countByIdPedido(Long pedidoId);
    
    /**
     * Conta itens por produto
     */
    long countByIdProduto(Long produtoId);
    
    /**
     * Busca itens ordenados por ID
     */
    List<ItemPedido> findByIdPedidoOrderById(Long pedidoId);
    
    /**
     * Verifica se existe item para um pedido específico
     */
    boolean existsByIdPedido(Long pedidoId);
    
    /**
     * Verifica se existe item para um produto específico
     */
    boolean existsByIdProduto(Long produtoId);
}