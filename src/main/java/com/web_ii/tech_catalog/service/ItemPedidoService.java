package com.web_ii.tech_catalog.service;

import com.web_ii.tech_catalog.models.ItemPedido;
import com.web_ii.tech_catalog.repository.ItemPedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemPedidoService {
    
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;
    
    /**
     * Salva um item de pedido
     */
    public ItemPedido salvar(ItemPedido itemPedido) {
        // Calcular valor total automaticamente
        itemPedido.calcularValorTotal();
        return itemPedidoRepository.save(itemPedido);
    }
    
    /**
     * Busca itens de um pedido
     */
    public List<ItemPedido> buscarItensPorPedido(Long pedidoId) {
        return itemPedidoRepository.findByIdPedido(pedidoId);
    }
    
    /**
     * Remove todos os itens de um pedido
     */
    public void removerItensPorPedido(Long pedidoId) {
        itemPedidoRepository.deleteByIdPedido(pedidoId);
    }
}