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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/pedidos")
// @PreAuthorize("hasRole('Admin')")
public class AdminPedidoController {
    
    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private ItemPedidoService itemPedidoService;
    
    @Autowired
    private TechCatalogService techCatalogService;
    
    /**
     * Lista todos os pedidos para administração
     */
    @GetMapping
    public String listarPedidos(Model model) {
        try {
            List<Pedido> pedidos = pedidoService.listarTodosPedidos();
            
            // Buscar dados dos usuários para exibir na lista
            Map<Long, User> usuarios = new HashMap<>();
            for (Pedido pedido : pedidos) {
                try {
                    Optional<User> userOptional = userService.findUserById(pedido.getIdUser());
                    if (userOptional.isPresent()) {
                        usuarios.put(pedido.getIdUser(), userOptional.get());
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao buscar usuário ID " + pedido.getIdUser() + ": " + e.getMessage());
                }
            }
            
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("usuarios", usuarios);
            
            return "admin/pedidos/lista";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar pedidos: " + e.getMessage());
            model.addAttribute("pedidos", List.of());
            model.addAttribute("usuarios", Map.of());
            return "admin/pedidos/lista";
        }
    }
    
    /**
     * Mostra detalhes de um pedido específico
     */
    @GetMapping("/{id}")
    public String detalhesPedido(@PathVariable Long id, Model model) {
        try {
            Pedido pedido = pedidoService.buscarPedidoPorId(id);
            
            if (pedido == null) {
                model.addAttribute("errorMessage", "Pedido não encontrado.");
                return "redirect:/admin/pedidos";
            }
            
            // Buscar dados do usuário
            Optional<User> userOptional = userService.findUserById(pedido.getIdUser());
            User usuario = userOptional.orElse(null);
            
            // Buscar itens do pedido
            List<ItemPedido> itens = itemPedidoService.buscarItensPorPedido(id);
            
            // Buscar dados dos produtos
            Map<Long, TechCatalog> produtos = new HashMap<>();
            for (ItemPedido item : itens) {
                try {
                    TechCatalog produto = techCatalogService.getTechCatalogById(item.getIdProduto());
                    if (produto != null) {
                        produtos.put(item.getIdProduto(), produto);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao buscar produto ID " + item.getIdProduto() + ": " + e.getMessage());
                }
            }
            
            model.addAttribute("pedido", pedido);
            model.addAttribute("usuario", usuario);
            model.addAttribute("itens", itens);
            model.addAttribute("produtos", produtos);
            
            return "admin/pedidos/detalhes";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar detalhes do pedido: " + e.getMessage());
            return "redirect:/admin/pedidos";
        }
    }
    
    /**
     * Página para editar um pedido
     */
    @GetMapping("/{id}/editar")
    public String editarPedido(@PathVariable Long id, Model model) {
        try {
            Pedido pedido = pedidoService.buscarPedidoPorId(id);
            
            if (pedido == null) {
                model.addAttribute("errorMessage", "Pedido não encontrado.");
                return "redirect:/admin/pedidos";
            }
            
            // Buscar dados do usuário
            Optional<User> userOptional = userService.findUserById(pedido.getIdUser());
            User usuario = userOptional.orElse(null);
            
            model.addAttribute("pedido", pedido);
            model.addAttribute("usuario", usuario);
            
            return "admin/pedidos/editar";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar pedido para edição: " + e.getMessage());
            return "redirect:/admin/pedidos";
        }
    }
    
    /**
     * Processa a edição de um pedido
     */
    @PostMapping("/{id}/editar")
    public String processarEdicao(@PathVariable Long id,
                                 @RequestParam String status,
                                 @RequestParam String enderecoEntrega,
                                 @RequestParam String formaPagamento,
                                 @RequestParam(required = false) String observacoes,
                                 RedirectAttributes redirectAttributes) {
        try {
            Pedido pedido = pedidoService.buscarPedidoPorId(id);
            
            if (pedido == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pedido não encontrado.");
                return "redirect:/admin/pedidos";
            }
            
            // Atualizar dados do pedido
            pedido.setStatus(status);
            pedido.setEnderecoEntrega(enderecoEntrega);
            pedido.setFormaPagamento(formaPagamento);
            pedido.setObservacoes(observacoes);
            
            pedidoService.atualizarPedido(pedido);
            
            redirectAttributes.addFlashAttribute("successMessage", "Pedido atualizado com sucesso!");
            return "redirect:/admin/pedidos/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar pedido: " + e.getMessage());
            return "redirect:/admin/pedidos/" + id + "/editar";
        }
    }
    
    /**
     * Atualiza apenas o status do pedido
     */
    @PostMapping("/{id}/status")
    public String atualizarStatus(@PathVariable Long id,
                                 @RequestParam String novoStatus,
                                 RedirectAttributes redirectAttributes) {
        try {
            pedidoService.atualizarStatusPedido(id, novoStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Status do pedido atualizado com sucesso!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar status: " + e.getMessage());
        }
        
        return "redirect:/admin/pedidos";
    }
    
    /**
     * Exclui um pedido
     */
    @PostMapping("/{id}/excluir")
    public String excluirPedido(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.excluirPedido(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pedido excluído com sucesso!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir pedido: " + e.getMessage());
        }
        
        return "redirect:/admin/pedidos";
    }
    
    /**
     * Cancela um pedido
     */
    @PostMapping("/{id}/cancelar")
    public String cancelarPedido(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.cancelarPedido(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pedido cancelado com sucesso!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cancelar pedido: " + e.getMessage());
        }
        
        return "redirect:/admin/pedidos/" + id;
    }
}