package com.web_ii.tech_catalog.config;

import com.web_ii.tech_catalog.service.TechCatalogService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public TechCatalogService techCatalogService() {
        return Mockito.mock(TechCatalogService.class);
    }

}
