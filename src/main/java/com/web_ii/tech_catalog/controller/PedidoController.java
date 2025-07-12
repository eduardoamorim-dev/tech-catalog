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
            User user = userService.findUserByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return "redirect:/login";
            }
            
            List<Pedido> pedidos = pedidoService.listarPedidosDoUsuario(user.getId().longValue());
            model.addAttribute("pedidos", pedidos);
            
            return "pedidos/lista";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar pedidos: " + e.getMessage());
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
            User user = userService.findUserByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return "redirect:/login";
            }
            
            Pedido pedido = pedidoService.buscarPedidoPorId(id);
            
            // Verifica se o pedido pertence ao usuário logado
            if (pedido == null || !pedido.getIdUser().equals(user.getId().longValue())) {
                model.addAttribute("errorMessage", "Pedido não encontrado.");
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
            
            return "pedidos/detalhes";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar detalhes do pedido: " + e.getMessage());
            return "redirect:/pedidos";
        }
    }
    
    /**
     * Cancela um pedido (apenas se estiver com status PENDENTE)
     */
    // @PostMapping("/{id}/cancelar")
    // public String cancelarPedido(@PathVariable Long id, Authentication authentication, 
    //                             RedirectAttributes redirectAttributes) {
    //     if (authentication == null || !authentication.isAuthenticated()) {
    //         return "redirect:/login";
    //     }
        
    //     try {
    //         User user = userService.findUserByEmail(authentication.getName()).orElse(null);
    //         if (user == null) {
    //             return "redirect:/login";
    //         }
            
    //         Pedido pedido = pedidoService.buscarPedidoPorId(id);
            
    //         if (pedido == null || !pedido.getIdUser().equals(user.getId().longValue())) {
    //             redirectAttributes.addFlashAttribute("errorMessage", "Pedido não encontrado.");
    //             return "redirect:/pedidos";
    //         }
            
    //         if (!"PENDENTE".equals(pedido.getStatus())) {
    //             redirectAttributes.addFlashAttribute("errorMessage", 
    //                 "Não é possível cancelar este pedido. Status atual: " + pedido.getStatus());
    //             return "redirect:/pedidos/" + id;
    //         }
            
    //         pedidoService.cancelarPedido(id);
    //         redirectAttributes.addFlashAttribute("successMessage", 
    //             "Pedido #" + id + " cancelado com sucesso!");
            
    //         return "redirect:/pedidos";
            
    //     } catch (Exception e) {
    //         redirectAttributes.addFlashAttribute("errorMessage", 
    //             "Erro ao cancelar pedido: " + e.getMessage());
    //         return "redirect:/pedidos";
    //     }
    // }
}