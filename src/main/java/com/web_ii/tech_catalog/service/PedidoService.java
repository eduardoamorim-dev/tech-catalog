package com.web_ii.tech_catalog.service;

import com.web_ii.tech_catalog.models.*;
import com.web_ii.tech_catalog.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private CarrinhoService carrinhoService;
    
    @Autowired
    private ItemPedidoService itemPedidoService;
    
    @Autowired
    private TechCatalogService produtoService;
    
    /**
     * Cria um pedido a partir do carrinho do usuário
     */
    @Transactional
    public Pedido criarPedidoDoCarrinho(Long userId, String enderecoEntrega, String formaPagamento, String observacoes) {
        // Buscar itens do carrinho
        List<Carrinho> itensCarrinho = carrinhoService.listarCarrinho(userId);
        
        if (itensCarrinho.isEmpty()) {
            throw new RuntimeException("Carrinho está vazio");
        }
        
        // Verificar estoque novamente
        for (Carrinho item : itensCarrinho) {
            if (item.getProduto().getNivelEstoque() < item.getQuantidade()) {
                throw new RuntimeException("Produto " + item.getProduto().getTitulo() + " não tem estoque suficiente");
            }
        }
        
        // Calcular valor total
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (Carrinho item : itensCarrinho) {
            BigDecimal valorItem = BigDecimal.valueOf(item.getProduto().getValorBase()).multiply(BigDecimal.valueOf(item.getQuantidade()));
            valorTotal = valorTotal.add(valorItem);
        }
        
        // Criar pedido
        Pedido pedido = new Pedido();
        pedido.setIdUser(userId);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setEnderecoEntrega(enderecoEntrega);
        pedido.setFormaPagamento(formaPagamento);
        pedido.setObservacoes(observacoes);
        pedido.setValorTotal(valorTotal);
        pedido.setStatus("PENDENTE");
        
        // Salvar pedido
        pedido = pedidoRepository.save(pedido);
        
        // Criar itens do pedido
        for (Carrinho item : itensCarrinho) {
            ItemPedido itemPedido = new ItemPedido(
                pedido.getId(),
                item.getIdProduto(),
                item.getQuantidade(),
                BigDecimal.valueOf(item.getProduto().getValorBase())
            );
            
            itemPedidoService.salvar(itemPedido);
            
            // Atualizar estoque
            TechCatalog produto = item.getProduto();
            produto.setNivelEstoque(produto.getNivelEstoque() - item.getQuantidade());
            produtoService.saveTechCatalog(produto);
        }
        
        // Limpar carrinho
        carrinhoService.limparCarrinho(userId);
        
        return pedido;
    }
    
    /**
     * Lista pedidos do usuário
     */
    public List<Pedido> listarPedidosDoUsuario(Long userId) {
        return pedidoRepository.findByIdUserOrderByDataPedidoDesc(userId);
    }
    
    /**
     * Busca pedido por ID
     */
    public Pedido buscarPedidoPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId).orElse(null);
    }
    
    /**
     * Cancela um pedido
     */
    @Transactional
    public void cancelarPedido(Long pedidoId) {
        Pedido pedido = buscarPedidoPorId(pedidoId);
        
        if (pedido == null) {
            throw new RuntimeException("Pedido não encontrado");
        }
        
        if (!"PENDENTE".equals(pedido.getStatus())) {
            throw new RuntimeException("Não é possível cancelar pedido com status: " + pedido.getStatus());
        }
        
        // Reverter estoque
        List<ItemPedido> itens = itemPedidoService.buscarItensPorPedido(pedidoId);
        for (ItemPedido item : itens) {
            TechCatalog produto = produtoService.getTechCatalogById(item.getIdProduto());
            if (produto != null) {
                produto.setNivelEstoque(produto.getNivelEstoque() + item.getQuantidade());
                produtoService.saveTechCatalog(produto);
            }
        }
        
        // Atualizar status
        pedido.setStatus("CANCELADO");
        pedidoRepository.save(pedido);
    }
}