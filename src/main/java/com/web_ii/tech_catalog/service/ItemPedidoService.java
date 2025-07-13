package com.web_ii.tech_catalog.service;

import com.web_ii.tech_catalog.models.ItemPedido;
import com.web_ii.tech_catalog.repository.ItemPedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemPedidoService {
    
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;
    
    /**
     * Salva um item de pedido
     */
    @Transactional
    public ItemPedido salvar(ItemPedido itemPedido) {
        try {
            // Calcular valor total automaticamente antes de salvar
            itemPedido.calcularValorTotal();
            return itemPedidoRepository.save(itemPedido);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar item do pedido: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca itens de um pedido
     */
    public List<ItemPedido> buscarItensPorPedido(Long pedidoId) {
        try {
            if (pedidoId == null) {
                throw new IllegalArgumentException("ID do pedido não pode ser nulo");
            }
            return itemPedidoRepository.findByIdPedido(pedidoId);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar itens do pedido: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca itens por produto
     */
    public List<ItemPedido> buscarItensPorProduto(Long produtoId) {
        try {
            if (produtoId == null) {
                throw new IllegalArgumentException("ID do produto não pode ser nulo");
            }
            return itemPedidoRepository.findByIdProduto(produtoId);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar itens por produto: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca um item específico por ID
     */
    public Optional<ItemPedido> buscarPorId(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("ID não pode ser nulo");
            }
            return itemPedidoRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar item por ID: " + e.getMessage(), e);
        }
    }
    
    /**
     * Atualiza um item de pedido
     */
    @Transactional
    public ItemPedido atualizar(ItemPedido itemPedido) {
        try {
            if (itemPedido.getId() == null) {
                throw new IllegalArgumentException("ID do item não pode ser nulo para atualização");
            }
            
            // Verificar se o item existe
            if (!itemPedidoRepository.existsById(itemPedido.getId())) {
                throw new RuntimeException("Item do pedido não encontrado");
            }
            
            // Calcular valor total automaticamente antes de atualizar
            itemPedido.calcularValorTotal();
            return itemPedidoRepository.save(itemPedido);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar item do pedido: " + e.getMessage(), e);
        }
    }
    
    /**
     * Remove todos os itens de um pedido
     */
    @Transactional
    public void removerItensPorPedido(Long pedidoId) {
        try {
            if (pedidoId == null) {
                throw new IllegalArgumentException("ID do pedido não pode ser nulo");
            }
            
            // Verificar se existem itens antes de tentar deletar
            List<ItemPedido> itens = itemPedidoRepository.findByIdPedido(pedidoId);
            if (!itens.isEmpty()) {
                itemPedidoRepository.deleteByIdPedido(pedidoId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover itens do pedido: " + e.getMessage(), e);
        }
    }
    
    /**
     * Remove um item específico
     */
    @Transactional
    public void removerItem(Long itemId) {
        try {
            if (itemId == null) {
                throw new IllegalArgumentException("ID do item não pode ser nulo");
            }
            
            if (!itemPedidoRepository.existsById(itemId)) {
                throw new RuntimeException("Item do pedido não encontrado");
            }
            
            itemPedidoRepository.deleteById(itemId);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover item do pedido: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifica se um item existe
     */
    public boolean existeItem(Long itemId) {
        try {
            if (itemId == null) {
                return false;
            }
            return itemPedidoRepository.existsById(itemId);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar existência do item: " + e.getMessage(), e);
        }
    }
    
    /**
     * Conta itens de um pedido
     */
    public long contarItensPorPedido(Long pedidoId) {
        try {
            if (pedidoId == null) {
                return 0;
            }
            return itemPedidoRepository.findByIdPedido(pedidoId).size();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao contar itens do pedido: " + e.getMessage(), e);
        }
    }
}