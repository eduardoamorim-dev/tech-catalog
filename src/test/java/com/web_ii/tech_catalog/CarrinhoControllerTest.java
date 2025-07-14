package com.web_ii.tech_catalog;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.web_ii.tech_catalog.controller.CarrinhoController;
import com.web_ii.tech_catalog.models.Carrinho;
import com.web_ii.tech_catalog.models.TechCatalog;
import com.web_ii.tech_catalog.models.User;
import com.web_ii.tech_catalog.service.CarrinhoService;
import com.web_ii.tech_catalog.service.IUserService;

@WebMvcTest(CarrinhoController.class)
public class CarrinhoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarrinhoService carrinhoService;

    @MockBean
    private IUserService userService;

    private User testUser;
    private TechCatalog testProduto;
    private Carrinho testCarrinho;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar usuário de teste
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("teste@email.com");
        testUser.setPassword("senha123");
        
        // Configurar produto de teste
        testProduto = new TechCatalog();
        testProduto.setId(1L);
        testProduto.setTitulo("Notebook Test");
        testProduto.setValorBase(1500.00);
        testProduto.setNivelEstoque(10);
        
        // Configurar carrinho de teste
        testCarrinho = new Carrinho();
        testCarrinho.setId(1L);
        testCarrinho.setIdUser(1L);
        testCarrinho.setIdProduto(1L);
        testCarrinho.setQuantidade(2);
        testCarrinho.setValorProduto(1500.00);
        testCarrinho.setProduto(testProduto);
    }

    @AfterEach
    void resetMocks() {
        reset(carrinhoService, userService);
    }

    @Test
    @DisplayName("GET /carrinho - Acesso sem autenticação deve retornar 401")
    void testExibirCarrinhoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/carrinho"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("GET /carrinho - Exibir carrinho com itens")
    void testExibirCarrinhoComItens() throws Exception {
        // Configurar mocks
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));
        when(carrinhoService.listarCarrinho(1L)).thenReturn(List.of(testCarrinho));
        
        CarrinhoService.CarrinhoResumo resumo = new CarrinhoService.CarrinhoResumo(1L, 2, BigDecimal.valueOf(3000.00));
        when(carrinhoService.obterResumo(1L)).thenReturn(resumo);

        mockMvc.perform(get("/carrinho"))
                .andExpect(status().isOk())
                .andExpect(view().name("carrinho/index"))
                .andExpect(model().attributeExists("itensCarrinho"))
                .andExpect(model().attributeExists("resumoCarrinho"))
                .andExpect(model().attribute("temItens", true))
                .andExpect(content().string(containsString("Notebook Test")));
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("GET /carrinho - Exibir carrinho vazio")
    void testExibirCarrinhoVazio() throws Exception {
        // Configurar mocks
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));
        when(carrinhoService.listarCarrinho(1L)).thenReturn(Collections.emptyList());
        
        CarrinhoService.CarrinhoResumo resumo = new CarrinhoService.CarrinhoResumo(0L, 0, BigDecimal.ZERO);
        when(carrinhoService.obterResumo(1L)).thenReturn(resumo);

        mockMvc.perform(get("/carrinho"))
                .andExpect(status().isOk())
                .andExpect(view().name("carrinho/index"))
                .andExpect(model().attribute("temItens", false));
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("POST /carrinho/adicionar - Adicionar produto com sucesso")
    void testAdicionarProdutoComSucesso() throws Exception {
        // Configurar mocks
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));
        when(carrinhoService.adicionarProduto(1L, 1L, 1)).thenReturn(testCarrinho);
        
        CarrinhoService.CarrinhoResumo resumo = new CarrinhoService.CarrinhoResumo(1L, 1, BigDecimal.valueOf(1500.00));
        when(carrinhoService.obterResumo(1L)).thenReturn(resumo);

        mockMvc.perform(post("/carrinho/adicionar")
                        .with(csrf())
                        .param("produtoId", "1")
                        .param("quantidade", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Produto adicionado ao carrinho com sucesso!"));

        verify(carrinhoService).adicionarProduto(1L, 1L, 1);
    }

    @Test
    @DisplayName("POST /carrinho/adicionar - Sem autenticação")
    void testAdicionarProdutoSemAutenticacao() throws Exception {
        mockMvc.perform(post("/carrinho/adicionar")
                        .with(csrf())
                        .param("produtoId", "1")
                        .param("quantidade", "1"))
                .andExpect(status().isUnauthorized());

        verify(carrinhoService, never()).adicionarProduto(anyLong(), anyLong(), any());
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("POST /carrinho/adicionar - Produto ID inválido")
    void testAdicionarProdutoIdInvalido() throws Exception {
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/carrinho/adicionar")
                        .with(csrf())
                        .param("produtoId", "0")
                        .param("quantidade", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("ID do produto inválido"));

        verify(carrinhoService, never()).adicionarProduto(anyLong(), anyLong(), any());
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("POST /carrinho/atualizar - Atualizar quantidade com sucesso")
    void testAtualizarQuantidadeComSucesso() throws Exception {
        // Configurar mocks
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));
        when(carrinhoService.atualizarQuantidade(1L, 1L, 3)).thenReturn(testCarrinho);
        
        CarrinhoService.CarrinhoResumo resumo = new CarrinhoService.CarrinhoResumo(1L, 3, BigDecimal.valueOf(4500.00));
        when(carrinhoService.obterResumo(1L)).thenReturn(resumo);

        mockMvc.perform(post("/carrinho/atualizar")
                        .with(csrf())
                        .param("produtoId", "1")
                        .param("quantidade", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Quantidade atualizada com sucesso!"));

        verify(carrinhoService).atualizarQuantidade(1L, 1L, 3);
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("POST /carrinho/remover - Remover item com sucesso")
    void testRemoverItemComSucesso() throws Exception {
        // Configurar mocks
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));
        
        CarrinhoService.CarrinhoResumo resumo = new CarrinhoService.CarrinhoResumo(0L, 0, BigDecimal.ZERO);
        when(carrinhoService.obterResumo(1L)).thenReturn(resumo);

        mockMvc.perform(post("/carrinho/remover")
                        .with(csrf())
                        .param("produtoId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Item removido do carrinho!"));

        verify(carrinhoService).removerItem(1L, 1L);
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("POST /carrinho/limpar - Limpar carrinho com sucesso")
    void testLimparCarrinhoComSucesso() throws Exception {
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/carrinho/limpar")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/carrinho"));

        verify(carrinhoService).limparCarrinho(1L);
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("GET /carrinho/resumo - Obter resumo do carrinho")
    void testObterResumoCarrinho() throws Exception {
        // Configurar mocks
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));
        
        CarrinhoService.CarrinhoResumo resumo = new CarrinhoService.CarrinhoResumo(2L, 5, BigDecimal.valueOf(7500.00));
        when(carrinhoService.obterResumo(1L)).thenReturn(resumo);

        mockMvc.perform(get("/carrinho/resumo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItens").value(2))
                .andExpect(jsonPath("$.totalQuantidade").value(5))
                .andExpect(jsonPath("$.valorTotal").value(7500.00));
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("GET /carrinho/verificar-estoque - Verificar estoque")
    void testVerificarEstoque() throws Exception {
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));
        when(carrinhoService.verificarEstoque(1L)).thenReturn(Collections.emptyList());
        when(carrinhoService.validarCarrinho(1L)).thenReturn(true);

        mockMvc.perform(get("/carrinho/verificar-estoque"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carrinhoValido").value(true))
                .andExpect(jsonPath("$.itensComProblema").isArray())
                .andExpect(jsonPath("$.itensComProblema", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("GET /carrinho/checkout - Checkout com carrinho válido")
    void testCheckoutComCarrinhoValido() throws Exception {
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));
        when(carrinhoService.listarCarrinho(1L)).thenReturn(List.of(testCarrinho));
        when(carrinhoService.validarCarrinho(1L)).thenReturn(true);
        
        CarrinhoService.CarrinhoResumo resumo = new CarrinhoService.CarrinhoResumo(1L, 2, BigDecimal.valueOf(3000.00));
        when(carrinhoService.obterResumo(1L)).thenReturn(resumo);

        mockMvc.perform(get("/carrinho/checkout"))
                .andExpect(status().isOk())
                .andExpect(view().name("carrinho/checkout"))
                .andExpect(model().attributeExists("itensCarrinho"))
                .andExpect(model().attributeExists("resumoCarrinho"));
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("GET /carrinho/checkout - Checkout com carrinho vazio")
    void testCheckoutComCarrinhoVazio() throws Exception {
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));
        when(carrinhoService.listarCarrinho(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/carrinho/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/carrinho"));
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("GET /carrinho/test - Endpoint de teste")
    void testEndpointTeste() throws Exception {
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/carrinho/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("CarrinhoController está funcionando!"))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("POST /carrinho/adicionar-json - Adicionar via JSON")
    void testAdicionarProdutoViaJson() throws Exception {
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));
        when(carrinhoService.adicionarProduto(1L, 1L, 2)).thenReturn(testCarrinho);
        
        CarrinhoService.CarrinhoResumo resumo = new CarrinhoService.CarrinhoResumo(1L, 2, BigDecimal.valueOf(3000.00));
        when(carrinhoService.obterResumo(1L)).thenReturn(resumo);

        String jsonRequest = "{\"produtoId\": 1, \"quantidade\": 2}";

        mockMvc.perform(post("/carrinho/adicionar-json")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Produto adicionado ao carrinho com sucesso!"));

        verify(carrinhoService).adicionarProduto(1L, 1L, 2);
    }

    @Test
    @WithMockUser(username = "teste@email.com")
    @DisplayName("POST /carrinho/adicionar - Erro ao adicionar produto")
    void testAdicionarProdutoComErro() throws Exception {
        when(userService.findUserByEmail("teste@email.com")).thenReturn(Optional.of(testUser));
        when(carrinhoService.adicionarProduto(1L, 1L, 1))
                .thenThrow(new RuntimeException("Estoque insuficiente"));

        mockMvc.perform(post("/carrinho/adicionar")
                        .with(csrf())
                        .param("produtoId", "1")
                        .param("quantidade", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Erro ao adicionar produto: Estoque insuficiente"));
    }
}