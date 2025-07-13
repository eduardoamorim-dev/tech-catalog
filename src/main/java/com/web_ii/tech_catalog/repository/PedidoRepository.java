package com.web_ii.tech_catalog.repository;

import com.web_ii.tech_catalog.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    /**
     * Busca pedidos por usuário ordenados por data (mais recentes primeiro)
     */
    List<Pedido> findByIdUserOrderByDataPedidoDesc(Long userId);
    
    /**
     * Busca todos os pedidos ordenados por data (mais recentes primeiro)
     */
    List<Pedido> findAllByOrderByDataPedidoDesc();
    
    /**
     * Busca pedidos por usuário e status
     */
    List<Pedido> findByIdUserAndStatus(Long userId, String status);
    
    /**
     * Busca pedidos por status
     */
    List<Pedido> findByStatus(String status);
    
    /**
     * Conta pedidos por usuário
     */
    long countByIdUser(Long userId);
    
    /**
     * Conta pedidos por usuário e status
     */
    long countByIdUserAndStatus(Long userId, String status);
}