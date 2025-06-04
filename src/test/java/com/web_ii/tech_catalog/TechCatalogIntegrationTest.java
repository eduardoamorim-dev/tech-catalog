package com.web_ii.tech_catalog;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.web_ii.tech_catalog.models.TechCatalog;
import com.web_ii.tech_catalog.repository.TechCatalogRepository;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Usa application-test.properties
@Transactional // Limpa o banco após cada teste
public class TechCatalogIntegrationTest {
    

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TechCatalogRepository techCatalogRepository;

    @Test
    @WithMockUser(authorities = { "Admin" })
    void testSaveTechCatalogIntegration() throws Exception {

        TechCatalog techCatalogA = new TechCatalog();
        techCatalogA.setCodigo("1234");
        techCatalogA.setTitulo("Produto A");
        techCatalogA.setValorBase(65.24);
        techCatalogA.setEspecificacaoTecnica("Especificação técnica do Produto A");
        techCatalogA.setCategoria("Categoria A");
        techCatalogA.setFabricante("Fabricante A");
        techCatalogA.setNivelEstoque("Alto");
        techCatalogA.setEstado("Novo");
        techCatalogA.setPrioridade("Alta");
        techCatalogA.setRecursosAdicionais("Recurso adicional A");
        techCatalogA.setLocaisDisponiveis(null); // Pode ser null se não houver locais disponíveis


        mockMvc.perform(post("/techcatalog/save")
                .with(csrf())
                .flashAttr("techcatalog", techCatalogA))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/techcatalog"));

        // Verifica no banco se foi salvo
        assertTrue(techCatalogRepository.findAll()
                .stream()
                .anyMatch(p -> "Produto A".equals(p.getTitulo())));
        
    }
}