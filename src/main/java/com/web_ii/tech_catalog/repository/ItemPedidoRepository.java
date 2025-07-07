package com.web_ii.tech_catalog.repository;

import com.web_ii.tech_catalog.models.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
     * Remove todos os itens de um pedido
     */
    void deleteByIdPedido(Long pedidoId);
}
