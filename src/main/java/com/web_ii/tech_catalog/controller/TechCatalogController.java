package com.web_ii.tech_catalog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.web_ii.tech_catalog.models.TechCatalog;
import com.web_ii.tech_catalog.service.TechCatalogService;


    @Controller
    public class TechCatalogController {
            @Autowired
        private TechCatalogService techCatalogService;

        @GetMapping("/techcatalog")
        public String index(Model model) {
            model.addAttribute("eletronicosList", techCatalogService.getAllTechCatalog());
            return "techcatalog/index";
        }
    }