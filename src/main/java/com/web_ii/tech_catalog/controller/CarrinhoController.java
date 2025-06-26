package com.web_ii.tech_catalog.controller;

import com.web_ii.tech_catalog.models.Carrinho;
import com.web_ii.tech_catalog.models.User;
import com.web_ii.tech_catalog.service.CarrinhoService;
import com.web_ii.tech_catalog.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {
    
    @Autowired
    private CarrinhoService carrinhoService;
    
    @Autowired
    private IUserService userService;

    /**
     * Exibe a página do carrinho com tratamento robusto de erros
     */
    @GetMapping
    public String exibirCarrinho(Model model, Authentication authentication) {
        System.out.println("=== INÍCIO exibirCarrinho ===");
        
        // Verificação de autenticação
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("❌ Usuário não autenticado, redirecionando para login");
            return "redirect:/login";
        }
        
        try {
            System.out.println("🔍 Obtendo userId...");
            Long userId = getUserId(authentication);
            System.out.println("✅ UserId obtido: " + userId);
            
            // Inicializa com valores padrão para evitar nulls
            List<Carrinho> itensCarrinho = Collections.emptyList();
            CarrinhoService.CarrinhoResumo resumo = null;
            
            try {
                System.out.println("🔍 Listando itens do carrinho...");
                itensCarrinho = carrinhoService.listarCarrinho(userId);
                if (itensCarrinho == null) {
                    System.out.println("⚠️ Lista de itens é null, usando lista vazia");
                    itensCarrinho = Collections.emptyList();
                } else {
                    System.out.println("✅ Encontrados " + itensCarrinho.size() + " itens no carrinho");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro ao listar carrinho: " + e.getMessage());
                e.printStackTrace();
                // Continua com lista vazia
            }
            
            try {
                System.out.println("🔍 Obtendo resumo do carrinho...");
                resumo = carrinhoService.obterResumo(userId);
                System.out.println("✅ Resumo obtido: " + (resumo != null ? resumo.toString() : "null"));
            } catch (Exception e) {
                System.err.println("❌ Erro ao obter resumo: " + e.getMessage());
                e.printStackTrace();
                // Cria resumo padrão
                // resumo = createDefaultResumo();
                System.out.println("✅ Resumo padrão criado");
            }
            
            // Adiciona atributos ao modelo
            System.out.println("🔍 Adicionando atributos ao modelo...");
            model.addAttribute("itensCarrinho", itensCarrinho);
            model.addAttribute("resumoCarrinho", resumo);
            model.addAttribute("temItens", itensCarrinho != null && !itensCarrinho.isEmpty());
            
            System.out.println("✅ Retornando página carrinho/index");
            return "carrinho/index";
            
        } catch (Exception e) {
            System.err.println("💥 Erro geral no carrinho: " + e.getMessage());
            e.printStackTrace();
            
            // Em caso de erro, exibe página com carrinho vazio
            model.addAttribute("errorMessage", "Erro ao carregar carrinho. Tente novamente.");
            model.addAttribute("itensCarrinho", Collections.emptyList());
            // model.addAttribute("resumoCarrinho", createDefaultResumo());
            model.addAttribute("temItens", false);
            
            System.out.println("✅ Retornando página carrinho/index com erro");
            return "carrinho/index";
        } finally {
            System.out.println("=== FIM exibirCarrinho ===");
        }
    }

    /**
     * Método auxiliar com logs melhorados
     */
    private Long getUserId(Authentication authentication) {
        System.out.println("🔍 === INÍCIO getUserId ===");
        
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("❌ Authentication é null ou não autenticado");
            throw new RuntimeException("Usuário não autenticado");
        }
        
        try {
            Object principal = authentication.getPrincipal();
            System.out.println("🔍 Principal class: " + (principal != null ? principal.getClass().getSimpleName() : "null"));
            
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String email = userDetails.getUsername(); // O username é o email
                System.out.println("🔍 Email do usuário: " + email);
                
                if (email == null || email.trim().isEmpty()) {
                    System.out.println("❌ Email é null ou vazio");
                    throw new RuntimeException("Email do usuário não encontrado");
                }
                
                System.out.println("🔍 Buscando usuário por email...");
                Optional<User> userOpt = userService.findUserByEmail(email);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    System.out.println("✅ Usuário encontrado: " + user.getEmail());
                    
                    if (user.getId() != null) {
                        Long userId = user.getId().longValue(); // Converte Integer para Long
                        System.out.println("✅ UserId: " + userId);
                        return userId;
                    } else {
                        System.out.println("❌ ID do usuário é null");
                        throw new RuntimeException("ID do usuário é null");
                    }
                } else {
                    System.out.println("❌ Usuário não encontrado no banco com email: " + email);
                    throw new RuntimeException("Usuário não encontrado no banco de dados com email: " + email);
                }
            } else {
                System.out.println("❌ Principal não é UserDetails: " + (principal != null ? principal.getClass().getSimpleName() : "null"));
                throw new RuntimeException("Principal não é uma instância de UserDetails. Tipo: " + 
                    (principal != null ? principal.getClass().getSimpleName() : "null"));
            }
            
        } catch (Exception e) {
            System.err.println("💥 Erro ao obter ID do usuário: " + e.getMessage());
            System.err.println("Authentication principal: " + authentication.getPrincipal());
            System.err.println("Authentication class: " + authentication.getClass().getSimpleName());
            e.printStackTrace();
            throw new RuntimeException("Erro ao identificar usuário: " + e.getMessage(), e);
        } finally {
            System.out.println("=== FIM getUserId ===");
        }
    }
    
    /**
     * Cria um resumo padrão para evitar erros de null
     */
    // private CarrinhoService.CarrinhoResumo createDefaultResumo() {
    //     try {
    //         // Cria uma instância do resumo com valores padrão (ajuste os valores conforme o construtor real)
    //         return new CarrinhoService.CarrinhoResumo(0, 0, 0.0);
    //     } catch (Exception e) {
    //         // Se falhar, retorna null e o template lidará com isso
    //         return null;
    //     }
    // }
    
    /**
     * Adiciona produto ao carrinho via AJAX
     */
    @PostMapping("/adicionar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> adicionarProduto(
            @RequestParam(name = "produtoId", required = true) Long produtoId,
            @RequestParam(name = "quantidade", defaultValue = "1") Integer quantidade,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Log para debug
            System.out.println("POST /carrinho/adicionar - produtoId: " + produtoId + ", quantidade: " + quantidade);
            
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Usuário não autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Validação de parâmetros
            if (produtoId == null || produtoId <= 0) {
                response.put("success", false);
                response.put("message", "ID do produto inválido");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (quantidade == null || quantidade <= 0) {
                quantidade = 1; // Default para 1
            }
            
            Long userId = getUserId(authentication);
            Carrinho carrinho = carrinhoService.adicionarProduto(userId, produtoId, quantidade);
            CarrinhoService.CarrinhoResumo resumo = carrinhoService.obterResumo(userId);
            
            response.put("success", true);
            response.put("message", "Produto adicionado ao carrinho com sucesso!");
            response.put("carrinhoResumo", resumo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Erro ao adicionar produto ao carrinho: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erro ao adicionar produto: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Alternative endpoint using RequestBody for JSON requests
     */
    @PostMapping(value = "/adicionar-json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> adicionarProdutoJson(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        try {
            Long produtoId = Long.valueOf(request.get("produtoId").toString());
            Integer quantidade = Integer.valueOf(request.getOrDefault("quantidade", 1).toString());
            
            return adicionarProduto(produtoId, quantidade, authentication);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Dados inválidos: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Atualiza quantidade de um item no carrinho
     */
    @PostMapping("/atualizar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> atualizarQuantidade(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Usuário não autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Long userId = getUserId(authentication);
            Carrinho carrinho = carrinhoService.atualizarQuantidade(userId, produtoId, quantidade);
            CarrinhoService.CarrinhoResumo resumo = carrinhoService.obterResumo(userId);
            
            response.put("success", true);
            response.put("message", "Quantidade atualizada com sucesso!");
            response.put("valorTotalItem", carrinho != null ? carrinho.getValorTotal() : 0.0);
            response.put("carrinhoResumo", resumo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Erro ao atualizar quantidade: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Erro ao atualizar: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Remove item do carrinho
     */
    @PostMapping("/remover")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removerItem(
            @RequestParam Long produtoId,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Usuário não autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Long userId = getUserId(authentication);
            carrinhoService.removerItem(userId, produtoId);
            
            // Tenta obter resumo, mas não falha se der erro
            CarrinhoService.CarrinhoResumo resumo = null;
            try {
                resumo = carrinhoService.obterResumo(userId);
            } catch (Exception e) {
                System.err.println("Erro ao obter resumo após remoção: " + e.getMessage());
            }
            
            response.put("success", true);
            response.put("message", "Item removido do carrinho!");
            response.put("carrinhoResumo", resumo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Erro ao remover item: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Erro ao remover item: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Limpa todo o carrinho
     */
    @PostMapping("/limpar")
    public String limparCarrinho(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Usuário não autenticado");
                return "redirect:/login";
            }
            
            Long userId = getUserId(authentication);
            carrinhoService.limparCarrinho(userId);
            redirectAttributes.addFlashAttribute("successMessage", "Carrinho limpo com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao limpar carrinho: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao limpar carrinho: " + e.getMessage());
        }
        
        return "redirect:/carrinho";
    }
    
    /**
     * Obtém resumo do carrinho para exibir na navbar
     */
    @GetMapping("/resumo")
    @ResponseBody
    public ResponseEntity<CarrinhoService.CarrinhoResumo> obterResumo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Long userId = getUserId(authentication);
            CarrinhoService.CarrinhoResumo resumo = carrinhoService.obterResumo(userId);
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            System.err.println("Erro ao obter resumo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Verifica estoque dos itens no carrinho
     */
    @GetMapping("/verificar-estoque")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verificarEstoque(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("error", "Usuário não autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Long userId = getUserId(authentication);
            List<Carrinho> itensComProblema = carrinhoService.verificarEstoque(userId);
            boolean carrinhoValido = carrinhoService.validarCarrinho(userId);
            
            response.put("carrinhoValido", carrinhoValido);
            response.put("itensComProblema", itensComProblema != null ? itensComProblema : Collections.emptyList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Erro ao verificar estoque: " + e.getMessage());
            response.put("error", "Erro ao verificar estoque: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Página de finalização de compra (placeholder)
     */
    @GetMapping("/checkout")
    public String checkout(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            Long userId = getUserId(authentication);
            List<Carrinho> itensCarrinho = carrinhoService.listarCarrinho(userId);
            
            // CORREÇÃO: Linha que estava causando erro de compilação
            if (itensCarrinho == null || itensCarrinho.isEmpty()) { // CORRIGIDO: era "itens Carrinho.isEmpty()"
                redirectAttributes.addFlashAttribute("errorMessage", "Seu carrinho está vazio!");
                return "redirect:/carrinho";
            }
            
            // Verifica se todos os itens têm estoque suficiente
            try {
                if (!carrinhoService.validarCarrinho(userId)) {
                    redirectAttributes.addFlashAttribute("errorMessage", 
                        "Alguns itens em seu carrinho não possuem estoque suficiente. Verifique e ajuste as quantidades.");
                    return "redirect:/carrinho";
                }
            } catch (Exception e) {
                System.err.println("Erro ao validar carrinho: " + e.getMessage());
                // Continue mesmo com erro de validação
            }
            
            CarrinhoService.CarrinhoResumo resumo = carrinhoService.obterResumo(userId);
            
            model.addAttribute("itensCarrinho", itensCarrinho);
            model.addAttribute("resumoCarrinho", resumo);
            
            return "carrinho/checkout";
        } catch (Exception e) {
            System.err.println("Erro no checkout: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao processar checkout: " + e.getMessage());
            return "redirect:/carrinho";
        }
    }
        
    
    /**
     * Endpoint de teste para verificar se o controller está funcionando
     */
    @GetMapping("/test")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> test(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CarrinhoController está funcionando!");
        response.put("timestamp", System.currentTimeMillis());
        
        if (authentication != null) {
            response.put("authenticated", authentication.isAuthenticated());
            response.put("principal", authentication.getPrincipal().toString());
            
            try {
                Long userId = getUserId(authentication);
                response.put("userId", userId);
            } catch (Exception e) {
                response.put("userIdError", e.getMessage());
            }
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint de teste POST
     */
    @PostMapping("/test-post")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testPost(@RequestParam(required = false) String param) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "POST funcionando!");
        response.put("param", param);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}