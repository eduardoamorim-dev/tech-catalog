package com.web_ii.tech_catalog.service;

import com.web_ii.tech_catalog.models.Carrinho;
import com.web_ii.tech_catalog.models.TechCatalog;
import com.web_ii.tech_catalog.repository.CarrinhoRepository;
import com.web_ii.tech_catalog.repository.TechCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarrinhoService {
    
    @Autowired
    private CarrinhoRepository carrinhoRepository;
    
    @Autowired
    private TechCatalogRepository eletronicosRepository;
    
    /**
     * Adiciona um produto ao carrinho
     */
    public Carrinho adicionarProduto(Long idUser, Long idProduto, Integer quantidade) {
        // Verifica se o produto existe
        TechCatalog produto = eletronicosRepository.findById(idProduto)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        
        // Verifica se há estoque suficiente
        if (produto.getNivelEstoque() < quantidade) {
            throw new RuntimeException("Estoque insuficiente. Disponível: " + produto.getNivelEstoque());
        }
        
        // Verifica se o item já existe no carrinho
        Optional<Carrinho> carrinhoExistente = carrinhoRepository.findByIdUserAndIdProduto(idUser, idProduto);
        
        if (carrinhoExistente.isPresent()) {
            // Se já existe, atualiza a quantidade
            Carrinho carrinho = carrinhoExistente.get();
            int novaQuantidade = carrinho.getQuantidade() + quantidade;
            
            // Verifica novamente o estoque com a nova quantidade
            if (produto.getNivelEstoque() < novaQuantidade) {
                throw new RuntimeException("Estoque insuficiente para a quantidade total solicitada. Disponível: " + 
                                         produto.getNivelEstoque() + ", no carrinho: " + carrinho.getQuantidade());
            }
            
            carrinho.setQuantidade(novaQuantidade);
            return carrinhoRepository.save(carrinho);
        } else {
            // Se não existe, cria um novo item
            Carrinho novoCarrinho = new Carrinho(idUser, idProduto, quantidade, produto.getValorBase());
            return carrinhoRepository.save(novoCarrinho);
        }
    }
    
    /**
     * Lista todos os itens do carrinho de um usuário
     */
    @Transactional(readOnly = true)
    public List<Carrinho> listarCarrinho(Long idUser) {
        return carrinhoRepository.findByIdUserWithProduto(idUser);
    }
    
    /**
     * Atualiza a quantidade de um item no carrinho
     */
    public Carrinho atualizarQuantidade(Long idUser, Long idProduto, Integer novaQuantidade) {
        if (novaQuantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }
        
        // Busca o item no carrinho
        Carrinho carrinho = carrinhoRepository.findByIdUserAndIdProduto(idUser, idProduto)
                .orElseThrow(() -> new RuntimeException("Item não encontrado no carrinho"));
        
        // Verifica o estoque
        TechCatalog produto = eletronicosRepository.findById(idProduto)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        
        if (produto.getNivelEstoque() < novaQuantidade) {
            throw new RuntimeException("Estoque insuficiente. Disponível: " + produto.getNivelEstoque());
        }
        
        carrinho.setQuantidade(novaQuantidade);
        return carrinhoRepository.save(carrinho);
    }
    
    /**
     * Remove um item do carrinho
     */
    public void removerItem(Long idUser, Long idProduto) {
        if (!carrinhoRepository.existsByIdUserAndIdProduto(idUser, idProduto)) {
            throw new RuntimeException("Item não encontrado no carrinho");
        }
        carrinhoRepository.deleteByIdUserAndIdProduto(idUser, idProduto);
    }
    
    /**
     * Limpa todo o carrinho de um usuário
     */
    public void limparCarrinho(Long idUser) {
        carrinhoRepository.deleteByIdUser(idUser);
    }
    
    /**
     * Calcula o total do carrinho
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalCarrinho(Long idUser) {
        return carrinhoRepository.sumValorTotalByUser(idUser);
    }
    
    /**
     * Conta quantos itens únicos há no carrinho
     */
    @Transactional(readOnly = true)
    public Long contarItens(Long idUser) {
        return carrinhoRepository.countItemsByUser(idUser);
    }
    
    /**
     * Soma a quantidade total de produtos no carrinho
     */
    @Transactional(readOnly = true)
    public Integer somarQuantidades(Long idUser) {
        return carrinhoRepository.sumQuantidadeByUser(idUser);
    }
    
    /**
     * Verifica se há itens com estoque insuficiente
     */
    @Transactional(readOnly = true)
    public List<Carrinho> verificarEstoque(Long idUser) {
        return carrinhoRepository.findItensComEstoqueInsuficiente(idUser);
    }
    
    /**
     * Valida todo o carrinho antes de finalizar compra
     */
    @Transactional(readOnly = true)
    public boolean validarCarrinho(Long idUser) {
        List<Carrinho> itens = carrinhoRepository.findByIdUserWithProduto(idUser);
        
        for (Carrinho item : itens) {
            if (item.getTechCatalog().getNivelEstoque() < item.getQuantidade()) {
                return false;
            }
        }
        
        return !itens.isEmpty();
    }
    
    /**
     * Busca um item específico do carrinho
     */
    @Transactional(readOnly = true)
    public Optional<Carrinho> buscarItem(Long idUser, Long idProduto) {
        return carrinhoRepository.findByIdUserAndIdProduto(idUser, idProduto);
    }
    
    /**
     * Obtém resumo do carrinho (para exibir na navbar)
     */
    @Transactional(readOnly = true)
    public CarrinhoResumo obterResumo(Long idUser) {
        Long totalItens = contarItens(idUser);
        Integer totalQuantidade = somarQuantidades(idUser);
        BigDecimal valorTotal = calcularTotalCarrinho(idUser);
        
        return new CarrinhoResumo(totalItens, totalQuantidade, valorTotal);
    }
    
    // Classe interna para resumo do carrinho
    public static class CarrinhoResumo {
        private Long totalItens;
        private Integer totalQuantidade;
        private BigDecimal valorTotal;
        
        public CarrinhoResumo(Long totalItens, Integer totalQuantidade, BigDecimal valorTotal) {
            this.totalItens = totalItens;
            this.totalQuantidade = totalQuantidade;
            this.valorTotal = valorTotal;
        }
        
        // Getters
        public Long getTotalItens() { return totalItens; }
        public Integer getTotalQuantidade() { return totalQuantidade; }
        public BigDecimal getValorTotal() { return valorTotal; }
    }
}