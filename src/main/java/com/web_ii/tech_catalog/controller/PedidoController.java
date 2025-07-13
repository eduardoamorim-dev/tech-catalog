package com.web_ii.tech_catalog.controller;

import com.web_ii.tech_catalog.models.ItemPedido;
import com.web_ii.tech_catalog.models.Pedido;
import com.web_ii.tech_catalog.models.TechCatalog;
import com.web_ii.tech_catalog.models.User;
import com.web_ii.tech_catalog.service.ItemPedidoService;
import com.web_ii.tech_catalog.service.PedidoService;
import com.web_ii.tech_catalog.service.TechCatalogService;
import com.web_ii.tech_catalog.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {
    
    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private ItemPedidoService itemPedidoService;
    
    @Autowired
    private TechCatalogService techCatalogService;
    
    /**
     * Lista todos os pedidos do usuário
     */
    @GetMapping
    public String listarPedidos(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            Optional<User> userOptional = userService.findUserByEmail(authentication.getName());
            if (!userOptional.isPresent()) {
                return "redirect:/login";
            }
            
            User user = userOptional.get();
            List<Pedido> pedidos = pedidoService.listarPedidosDoUsuario(user.getId().longValue());
            
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("user", user);
            
            return "pedidos/lista";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar pedidos: " + e.getMessage());
            model.addAttribute("pedidos", List.of()); // Lista vazia para evitar erro no template
            return "pedidos/lista";
        }
    }
    
    /**
     * Mostra detalhes de um pedido específico
     */
    @GetMapping("/{id}")
    public String detalhesPedido(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            Optional<User> userOptional = userService.findUserByEmail(authentication.getName());
            if (!userOptional.isPresent()) {
                return "redirect:/login";
            }
            
            User user = userOptional.get();
            Pedido pedido = pedidoService.buscarPedidoPorId(id);
            
            // Verifica se o pedido existe e pertence ao usuário logado
            if (pedido == null || !pedido.getIdUser().equals(user.getId().longValue())) {
                model.addAttribute("errorMessage", "Pedido não encontrado ou você não tem permissão para visualizá-lo.");
                return "redirect:/pedidos";
            }
            
            // Buscar itens do pedido
            List<ItemPedido> itens = itemPedidoService.buscarItensPorPedido(id);
            
            // Buscar dados dos produtos e criar um mapa para facilitar o acesso no template
            Map<Long, TechCatalog> produtos = new HashMap<>();
            for (ItemPedido item : itens) {
                try {
                    TechCatalog produto = techCatalogService.getTechCatalogById(item.getIdProduto());
                    if (produto != null) {
                        produtos.put(item.getIdProduto(), produto);
                    }
                } catch (Exception e) {
                    // Log do erro, mas continua processando outros itens
                    System.err.println("Erro ao buscar produto ID " + item.getIdProduto() + ": " + e.getMessage());
                }
            }
            
            model.addAttribute("pedido", pedido);
            model.addAttribute("itens", itens);
            model.addAttribute("produtos", produtos);
            model.addAttribute("user", user);
            
            return "pedidos/detalhes";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar detalhes do pedido: " + e.getMessage());
            return "redirect:/pedidos";
        }
    }
    
    /**
     * Cancelar um pedido
     */
    @PostMapping("/{id}/cancelar")
    public String cancelarPedido(@PathVariable Long id, 
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            Optional<User> userOptional = userService.findUserByEmail(authentication.getName());
            if (!userOptional.isPresent()) {
                return "redirect:/login";
            }
            
            User user = userOptional.get();
            Pedido pedido = pedidoService.buscarPedidoPorId(id);
            
            // Verifica se o pedido existe e pertence ao usuário logado
            if (pedido == null || !pedido.getIdUser().equals(user.getId().longValue())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pedido não encontrado ou você não tem permissão para cancelá-lo.");
                return "redirect:/pedidos";
            }
            
            // Verificar se o pedido pode ser cancelado
            if (!"PENDENTE".equals(pedido.getStatus())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Só é possível cancelar pedidos com status PENDENTE.");
                return "redirect:/pedidos/" + id;
            }
            
            pedidoService.cancelarPedido(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pedido cancelado com sucesso!");
            
            return "redirect:/pedidos/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cancelar pedido: " + e.getMessage());
            return "redirect:/pedidos";
        }
    }
    
    /**
     * Página de checkout (criar pedido)
     */
    @GetMapping("/checkout")
    public String checkout(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            Optional<User> userOptional = userService.findUserByEmail(authentication.getName());
            if (!userOptional.isPresent()) {
                return "redirect:/login";
            }
            
            User user = userOptional.get();
            model.addAttribute("user", user);
            
            return "pedidos/checkout";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar página de checkout: " + e.getMessage());
            return "redirect:/carrinho";
        }
    }
    
    /**
     * Processar checkout (criar pedido)
     */
    @PostMapping("/checkout")
    public String processarCheckout(@RequestParam String enderecoEntrega,
                                   @RequestParam String formaPagamento,
                                   @RequestParam(required = false) String observacoes,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            Optional<User> userOptional = userService.findUserByEmail(authentication.getName());
            if (!userOptional.isPresent()) {
                return "redirect:/login";
            }
            
            User user = userOptional.get();
            
            // Criar pedido a partir do carrinho
            Pedido pedido = pedidoService.criarPedidoDoCarrinho(
                user.getId().longValue(), 
                enderecoEntrega, 
                formaPagamento, 
                observacoes
            );
            
            redirectAttributes.addFlashAttribute("successMessage", "Pedido criado com sucesso! Número do pedido: " + pedido.getId());
            
            return "redirect:/pedidos/" + pedido.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar pedido: " + e.getMessage());
            return "redirect:/pedidos/checkout";
        }
    }
}