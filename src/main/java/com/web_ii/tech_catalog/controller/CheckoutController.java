package com.web_ii.tech_catalog.controller;

import com.web_ii.tech_catalog.models.*;
import com.web_ii.tech_catalog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    
    private static final Logger logger = Logger.getLogger(CheckoutController.class.getName());
    
    @Autowired
    private CarrinhoService carrinhoService;
    
    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private IUserService userService;
    
    /**
     * Página de checkout otimizada
     */
    @GetMapping
    public String checkout(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Validação de autenticação
            if (!isAuthenticated(authentication)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para fazer checkout.");
                return "redirect:/login";
            }
            
            // Buscar usuário
            User user = getUserFromAuth(authentication);
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
                return "redirect:/login";
            }
            
            Long userId = user.getId().longValue();
            
            // Buscar itens do carrinho com validação
            List<Carrinho> itensCarrinho = carrinhoService.listarCarrinho(userId);
            if (itensCarrinho == null || itensCarrinho.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Seu carrinho está vazio.");
                return "redirect:/carrinho";
            }
            
            // Validar produtos e estoque
            String validationError = validateCartItems(itensCarrinho);
            if (validationError != null) {
                redirectAttributes.addFlashAttribute("errorMessage", validationError);
                return "redirect:/carrinho";
            }
            
            // Buscar resumo do carrinho
            CarrinhoService.CarrinhoResumo resumo = null;
            try {
                resumo = carrinhoService.obterResumo(userId);
            } catch (Exception e) {
                logger.warning("Erro ao obter resumo do carrinho: " + e.getMessage());
                // Continue sem resumo, será calculado no frontend
            }
            
            // Adicionar dados ao modelo
            model.addAttribute("user", user);
            model.addAttribute("itensCarrinho", itensCarrinho);
            model.addAttribute("resumoCarrinho", resumo);
            
            return "checkout";
            
        } catch (Exception e) {
            logger.severe("Erro no checkout: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erro interno. Tente novamente.");
            return "redirect:/carrinho";
        }
    }
    
    /**
     * Processar pedido otimizado
     */
    @PostMapping("/processar")
    public String processarPedido(
            @RequestParam("enderecoEntrega") String enderecoEntrega,
            @RequestParam("formaPagamento") String formaPagamento,
            @RequestParam(value = "observacoes", required = false) String observacoes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validação de autenticação
            if (!isAuthenticated(authentication)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada. Faça login novamente.");
                return "redirect:/login";
            }
            
            // Buscar usuário
            User user = getUserFromAuth(authentication);
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
                return "redirect:/login";
            }
            
            // Validar dados de entrada
            String validationError = validateOrderData(enderecoEntrega, formaPagamento);
            if (validationError != null) {
                redirectAttributes.addFlashAttribute("errorMessage", validationError);
                return "redirect:/checkout";
            }
            
            Long userId = user.getId().longValue();
            
            // Verificar se ainda há itens no carrinho
            List<Carrinho> itensCarrinho = carrinhoService.listarCarrinho(userId);
            if (itensCarrinho == null || itensCarrinho.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Seu carrinho está vazio.");
                return "redirect:/carrinho";
            }
            
            // Validar estoque novamente antes de processar
            String stockError = validateCartItems(itensCarrinho);
            if (stockError != null) {
                redirectAttributes.addFlashAttribute("errorMessage", stockError);
                return "redirect:/carrinho";
            }
            
            // Criar pedido
            Pedido pedido = pedidoService.criarPedidoDoCarrinho(
                userId,
                enderecoEntrega.trim(),
                formaPagamento,
                observacoes != null ? observacoes.trim() : null
            );
            
            logger.info("Pedido criado com sucesso: " + pedido.getId() + " para usuário: " + user.getEmail());
            
            redirectAttributes.addFlashAttribute("successMessage",
                "Pedido #" + pedido.getId() + " criado com sucesso!");
            
            return "redirect:/pedidos";
            
        } catch (Exception e) {
            logger.severe("Erro ao processar pedido: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                "Erro ao processar pedido. Tente novamente.");
            return "redirect:/checkout";
        }
    }
    
    /**
     * Método auxiliar para debug (opcional)
     */
    @GetMapping("/debug")
    public String debugCheckout(Authentication authentication, Model model) {
        if (!isAuthenticated(authentication)) {
            return "redirect:/login";
        }
        
        try {
            User user = getUserFromAuth(authentication);
            if (user == null) {
                model.addAttribute("error", "Usuário não encontrado");
                return "debug";
            }
            
            Long userId = user.getId().longValue();
            List<Carrinho> itensCarrinho = carrinhoService.listarCarrinho(userId);
            CarrinhoService.CarrinhoResumo resumo = carrinhoService.obterResumo(userId);
            
            model.addAttribute("user", user);
            model.addAttribute("itensCarrinho", itensCarrinho);
            model.addAttribute("resumoCarrinho", resumo);
            model.addAttribute("debug", true);
            
            return "checkout";
            
        } catch (Exception e) {
            model.addAttribute("error", "Erro: " + e.getMessage());
            return "debug";
        }
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * Verificar se o usuário está autenticado
     */
    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }
    
    /**
     * Obter usuário a partir da autenticação
     */
    private User getUserFromAuth(Authentication authentication) {
        try {
            return userService.findUserByEmail(authentication.getName()).orElse(null);
        } catch (Exception e) {
            logger.warning("Erro ao buscar usuário: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Validar itens do carrinho
     */
    private String validateCartItems(List<Carrinho> itensCarrinho) {
        for (Carrinho item : itensCarrinho) {
            // Verificar se produto existe
            if (item.getProduto() == null) {
                return "Produto não encontrado no carrinho.";
            }
            
            // Verificar quantidade válida
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                return "Quantidade inválida para o produto: " + item.getProduto().getTitulo();
            }
            
            // Verificar estoque
            if (item.getProduto().getNivelEstoque() < item.getQuantidade()) {
                return "Produto '" + item.getProduto().getTitulo() + "' não tem estoque suficiente. " +
                       "Disponível: " + item.getProduto().getNivelEstoque() + 
                       ", Solicitado: " + item.getQuantidade();
            }
            
            // Verificar se produto está ativo
            // if (!item.getProduto().getAtivo()) {
            //     return "Produto '" + item.getProduto().getTitulo() + "' não está mais disponível.";
            // }
            
            // Verificar preço válido
            if (item.getProduto().getValorBase() == null || new BigDecimal(item.getProduto().getValorBase().toString()).compareTo(BigDecimal.ZERO) <= 0) {
                return "Preço inválido para o produto: " + item.getProduto().getTitulo();
            }
        }
        
        return null; // Tudo válido
    }
    
    /**
     * Validar dados do pedido
     */
    private String validateOrderData(String enderecoEntrega, String formaPagamento) {
        // Validar endereço
        if (enderecoEntrega == null || enderecoEntrega.trim().isEmpty()) {
            return "Endereço de entrega é obrigatório.";
        }
        
        if (enderecoEntrega.trim().length() < 20) {
            return "Endereço de entrega deve ter pelo menos 20 caracteres.";
        }
        
        // Validar forma de pagamento
        if (formaPagamento == null || formaPagamento.trim().isEmpty()) {
            return "Forma de pagamento é obrigatória.";
        }
        
        // Validar formas de pagamento aceitas
        if (!isValidPaymentMethod(formaPagamento)) {
            return "Forma de pagamento inválida.";
        }
        
        return null; // Tudo válido
    }
    
    /**
     * Verificar se forma de pagamento é válida
     */
    private boolean isValidPaymentMethod(String formaPagamento) {
        String[] validMethods = {"cartao", "pix", "dinheiro", "transferencia"};
        for (String method : validMethods) {
            if (method.equalsIgnoreCase(formaPagamento.trim())) {
                return true;
            }
        }
        return false;
    }
}