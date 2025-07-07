package com.web_ii.tech_catalog.controller;

import com.web_ii.tech_catalog.models.*;
import com.web_ii.tech_catalog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    
    @Autowired
    private CarrinhoService carrinhoService;
    
    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private IUserService userService;
    
    /**
     * Página de checkout - mostra formulário com dados do carrinho
     */
    @GetMapping
    public String checkout(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("=== INÍCIO checkout ===");
        
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para fazer checkout.");
            return "redirect:/login";
        }
        
        try {
            User user = userService.findUserByEmail(authentication.getName()).orElse(null);
            System.out.println("Usuário encontrado: " + (user != null ? user.getEmail() : "null"));
            
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
                return "redirect:/login";
            }
            
            Long userId = user.getId().longValue();
            System.out.println("UserId: " + userId);
            
            // Buscar itens do carrinho
            List<Carrinho> itensCarrinho = carrinhoService.listarCarrinho(userId);
            System.out.println("Itens no carrinho: " + (itensCarrinho != null ? itensCarrinho.size() : "null"));
            
            // Log detalhado dos itens
            if (itensCarrinho != null && !itensCarrinho.isEmpty()) {
                for (int i = 0; i < itensCarrinho.size(); i++) {
                    Carrinho item = itensCarrinho.get(i);
                    System.out.println("Item " + i + ": " + 
                        (item.getProduto() != null ? item.getProduto().getTitulo() : "Produto null") + 
                        " - Qtd: " + item.getQuantidade() + 
                        " - Preço: " + (item.getProduto() != null ? item.getProduto().getValorBase() : "null"));
                }
            }
            
            if (itensCarrinho == null || itensCarrinho.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Seu carrinho está vazio.");
                return "redirect:/carrinho";
            }
            
            // Verificar estoque antes de mostrar checkout
            for (Carrinho item : itensCarrinho) {
                if (item.getProduto() == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Produto não encontrado no carrinho.");
                    return "redirect:/carrinho";
                }
                
                if (item.getProduto().getNivelEstoque() < item.getQuantidade()) {
                    redirectAttributes.addFlashAttribute("errorMessage", 
                        "Produto " + item.getProduto().getTitulo() + " não tem estoque suficiente.");
                    return "redirect:/carrinho";
                }
            }
            
            // Buscar resumo do carrinho
            CarrinhoService.CarrinhoResumo resumo = null;
            try {
                resumo = carrinhoService.obterResumo(userId);
                System.out.println("Resumo obtido: " + (resumo != null ? resumo.toString() : "null"));
            } catch (Exception e) {
                System.err.println("Erro ao obter resumo: " + e.getMessage());
                // Continua sem resumo
            }
            
            // Adicionar atributos ao modelo
            model.addAttribute("user", user);
            model.addAttribute("itensCarrinho", itensCarrinho);
            model.addAttribute("resumoCarrinho", resumo);
            
            // Adicionar debug info se necessário
            if (System.getProperty("debug") != null) {
                model.addAttribute("debug", true);
            }
            
            System.out.println("Retornando template checkout");
            return "checkout";
            
        } catch (Exception e) {
            System.err.println("Erro no checkout: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao carregar checkout: " + e.getMessage());
            return "redirect:/carrinho";
        } finally {
            System.out.println("=== FIM checkout ===");
        }
    }
    
    /**
     * Processar pedido - versão simplificada
     */
    @PostMapping("/processar")
    public String processarPedido(
            @RequestParam("enderecoEntrega") String enderecoEntrega,
            @RequestParam("formaPagamento") String formaPagamento,
            @RequestParam(value = "observacoes", required = false) String observacoes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("=== PROCESSAR PEDIDO ===");
        System.out.println("Endereço: " + enderecoEntrega);
        System.out.println("Forma pagamento: " + formaPagamento);
        System.out.println("Observações: " + observacoes);
        
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado.");
            return "redirect:/login";
        }
        
        try {
            User user = userService.findUserByEmail(authentication.getName()).orElse(null);
            
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
                return "redirect:/login";
            }
            
            // Validar dados obrigatórios
            if (enderecoEntrega == null || enderecoEntrega.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Endereço de entrega é obrigatório.");
                return "redirect:/checkout";
            }
            
            if (formaPagamento == null || formaPagamento.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Forma de pagamento é obrigatória.");
                return "redirect:/checkout";
            }
            
            // Verificar se ainda há itens no carrinho
            Long userId = user.getId().longValue();
            List<Carrinho> itensCarrinho = carrinhoService.listarCarrinho(userId);
            
            if (itensCarrinho == null || itensCarrinho.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Seu carrinho está vazio.");
                return "redirect:/carrinho";
            }
            
            // Criar pedido
            Pedido pedido = pedidoService.criarPedidoDoCarrinho(
                userId, 
                enderecoEntrega.trim(), 
                formaPagamento, 
                observacoes != null ? observacoes.trim() : null
            );
            
            System.out.println("Pedido criado: " + pedido.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Pedido #" + pedido.getId() + " criado com sucesso!");
            
            return "redirect:/pedidos";
            
        } catch (Exception e) {
            System.err.println("Erro ao processar pedido: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erro ao processar pedido: " + e.getMessage());
            return "redirect:/checkout";
        }
    }
    
    /**
     * Método auxiliar para debug
     */
    @GetMapping("/debug")
    public String debugCheckout(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            User user = userService.findUserByEmail(authentication.getName()).orElse(null);
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
}