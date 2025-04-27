package com.web_ii.tech_catalog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

        @GetMapping("/techcatalog/create")
        public String create(Model model) {
            model.addAttribute("techcatalog", new TechCatalog());
            return "techcatalog/create";
        }

        @PostMapping("/techcatalog/save")
        public String postMethodName(@ModelAttribute("techcatalog") TechCatalog techcatalog) {
            techCatalogService.saveTechCatalog(techcatalog);
            return "redirect:/techcatalog";
        }
    }