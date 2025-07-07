package com.web_ii.tech_catalog.repository;

import com.web_ii.tech_catalog.models.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    
    /**
     * Busca todos os itens do carrinho de um usuário específico
     */
    List<Carrinho> findByIdUserOrderByDataAdicaoDesc(Long idUser);
    
    /**
     * Busca um item específico do carrinho por usuário e produto
     */
    Optional<Carrinho> findByIdUserAndIdProduto(Long idUser, Long idProduto);
    
    /**
     * Verifica se existe um item no carrinho para um usuário e produto específicos
     */
    boolean existsByIdUserAndIdProduto(Long idUser, Long idProduto);
    
    /**
     * Conta quantos itens únicos existem no carrinho de um usuário
     */
    @Query("SELECT COUNT(c) FROM Carrinho c WHERE c.idUser = :idUser")
    Long countItemsByUser(@Param("idUser") Long idUser);
    
    /**
     * Soma a quantidade total de produtos no carrinho de um usuário
     */
    @Query("SELECT COALESCE(SUM(c.quantidade), 0) FROM Carrinho c WHERE c.idUser = :idUser")
    Integer sumQuantidadeByUser(@Param("idUser") Long idUser);
    
    /**
     * Calcula o valor total do carrinho de um usuário
     */
    @Query("SELECT COALESCE(SUM(c.valorTotal), 0) FROM Carrinho c WHERE c.idUser = :idUser")
    BigDecimal sumValorTotalByUser(@Param("idUser") Long idUser);
    
    /**
     * Busca itens do carrinho com informações do produto (JOIN)
     */
    @Query("SELECT c FROM Carrinho c " +
           "JOIN FETCH c.produto p " +
           "WHERE c.idUser = :idUser " +
           "ORDER BY c.dataAdicao DESC")
    List<Carrinho> findByIdUserWithProduto(@Param("idUser") Long idUser);
    
    /**
     * Remove todos os itens do carrinho de um usuário
     */
    void deleteByIdUser(Long idUser);
    
    /**
     * Remove um item específico do carrinho
     */
    void deleteByIdUserAndIdProduto(Long idUser, Long idProduto);
    
    /**
     * Busca carrinho com produto e usuário para exibição completa
     */
    @Query("SELECT c FROM Carrinho c " +
           "JOIN FETCH c.produto p " +
           "JOIN FETCH c.user u " +
           "WHERE c.idUser = :idUser " +
           "ORDER BY c.dataAdicao DESC")
    List<Carrinho> findCarrinhoCompletoByUser(@Param("idUser") Long idUser);
    
    /**
     * Busca produtos que estão em falta no estoque mas estão no carrinho
     */
    @Query("SELECT c FROM Carrinho c " +
           "JOIN c.produto p " +
           "WHERE c.idUser = :idUser " +
           "AND p.nivelEstoque < c.quantidade")
    List<Carrinho> findItensComEstoqueInsuficiente(@Param("idUser") Long idUser);
    
    /**
     * Atualiza a quantidade de um item no carrinho
     */
    @Query("UPDATE Carrinho c SET c.quantidade = :quantidade, c.valorTotal = c.valorProduto * :quantidade " +
           "WHERE c.idUser = :idUser AND c.idProduto = :idProduto")
    int updateQuantidade(@Param("idUser") Long idUser, 
                        @Param("idProduto") Long idProduto, 
                        @Param("quantidade") Integer quantidade);
}

