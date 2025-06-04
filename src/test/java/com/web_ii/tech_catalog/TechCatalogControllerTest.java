package com.web_ii.tech_catalog;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.web_ii.tech_catalog.controller.TechCatalogController;
import com.web_ii.tech_catalog.models.TechCatalog;
import com.web_ii.tech_catalog.service.TechCatalogService;
import com.web_ii.tech_catalog.config.TestConfig;

@WebMvcTest(TechCatalogController.class)
@Import(TestConfig.class)
public class TechCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TechCatalogService techCatalogService;

    @AfterEach
    void resetMocks() {
        reset(techCatalogService);
    }

    private List<TechCatalog> testCreateTechCatalogList(){
        TechCatalog techCatalogB = new TechCatalog();
        techCatalogB.setId(1L);
        techCatalogB.setCodigo("1234");
        techCatalogB.setTitulo("Produto B");
        techCatalogB.setValorBase(65.24);
        techCatalogB.setEspecificacaoTecnica("Especificação técnica do Produto B");
        techCatalogB.setCategoria("Categoria B");
        techCatalogB.setFabricante("Fabricante B");
        techCatalogB.setNivelEstoque("Alto");
        techCatalogB.setEstado("Novo");
        techCatalogB.setPrioridade("Alta");
        techCatalogB.setRecursosAdicionais("Recurso adicional B");
        techCatalogB.setLocaisDisponiveis(null); // Pode ser null se não houver locais disponíveis
        return List.of(techCatalogB);
    }

    @Test
    @DisplayName("GET /techcatalog - Listar eletronicos na tela index sem usuário autenticado")
    void testIndexNotAuthenticatedUser() throws Exception {
         mockMvc.perform(get("/techCatalog"))
            .andExpect(status().isUnauthorized()); // Correção aqui
    }

    @Test
    @WithMockUser
    @DisplayName("GET /techcatalog - Listar eletronicos na tela index com usuário logado")
    void testIndexAuthenticatedUser() throws Exception {
        when(techCatalogService.getAllTechCatalog()).thenReturn(testCreateTechCatalogList());

        mockMvc.perform(get("/techcatalog"))
               .andExpect(status().isOk())
               .andExpect(view().name("techcatalog/index"))
               .andExpect(model().attributeExists("techCatalogList"))
               .andExpect(content().string(containsString("Listagem de Eletrônicos")))
               .andExpect(content().string(containsString("Produto B")));
    }

    @Test
    @WithMockUser(username = "testeadmin@gmail.com", authorities = { "Admin" })
    @DisplayName("GET /techcatalog/create - Exibe formulário de criação")
    void testCreateFormAuthorizedUser() throws Exception {
        mockMvc.perform(get("/techcatalog/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("techcatalog/create"))
                .andExpect(model().attributeExists("techcatalog"))
                .andExpect(content().string(containsString("Cadastrar Eletrônico")));
    }

    @Test
    @WithMockUser(username = "testemanager@gmail.com", authorities = { "Manager" })
    @DisplayName("GET /techcatalog - Verificar o link de cadastrar para um usuario não admin logado")
    void testCreateFormNotAuthorizedUser() throws Exception {
        when(techCatalogService.getAllTechCatalog()).thenReturn(testCreateTechCatalogList());
       // Obter o HTML da página renderizada pelo controlador
       mockMvc.perform(get("/techcatalog/create"))
            .andExpect(status().isOk())
            .andExpect(view().name("techcatalog/create"))
            .andExpect(model().attributeExists("techcatalog"))
            .andExpect(content().string(not(containsString("<a class=\"dropdown-item\" href=\"/techcatalog/create\">Cadastrar</a>"))));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /techcatalog/save - Falha na validação e retorna para o formulário")
    void testSaveTechCatalogValidationError() throws Exception {
        TechCatalog techCatalog = new TechCatalog(); // Produto sem nome, o que causará erro de validação

        mockMvc.perform(post("/techcatalog/save")
                        .with(csrf())
                        .flashAttr("techcatalog", techCatalog))
                .andExpect(status().isOk())
                .andExpect(view().name("techcatalog/create"))
                .andExpect(model().attributeHasErrors("techcatalog"));

        verify(techCatalogService, never()).saveTechCatalog(any(TechCatalog.class));
    }

    @Test
    @WithMockUser(username = "testeadmin@gmail.com", authorities = { "Admin" })
    @DisplayName("POST /techcatalog/save - Eletronico válido é salvo com sucesso")
    void testSaveValidTechCatalog() throws Exception {
        TechCatalog techCatalog = new TechCatalog();
        techCatalog.setCodigo("1234");
        techCatalog.setTitulo("Eletronico");
        techCatalog.setValorBase(100.24);
        techCatalog.setEspecificacaoTecnica("Especificação técnica do Eletronico");
        techCatalog.setCategoria("Categoria");
        techCatalog.setFabricante("Fabricante");
        techCatalog.setNivelEstoque("Alto");
        techCatalog.setEstado("Novo");
        techCatalog.setPrioridade("Alta");
        techCatalog.setRecursosAdicionais("Recurso adicional");
        techCatalog.setLocaisDisponiveis(null); // Pode ser null se não houver locais disponíveis

        mockMvc.perform(post("/techcatalog/save")
                        .with(csrf())
                        .flashAttr("techcatalog", techCatalog))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/techcatalog"));

        verify(techCatalogService).saveTechCatalog(any(TechCatalog.class));
    }

}
