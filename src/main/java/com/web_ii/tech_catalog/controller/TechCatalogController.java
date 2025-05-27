package com.web_ii.tech_catalog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.web_ii.tech_catalog.models.TechCatalog;
import com.web_ii.tech_catalog.service.TechCatalogService;

import jakarta.validation.Valid;


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
        public String postMethodName(@ModelAttribute @Valid TechCatalog techcatalog, BindingResult result) {
            if(result.hasErrors()) {
                return "techcatalog/create";
            }
            techCatalogService.saveTechCatalog(techcatalog);
            return "redirect:/techcatalog";
        }

        @GetMapping("/techcatalog/delete/{id}")
        public String delete(@PathVariable Long id) {
            this.techCatalogService.deleteTechCatalogById(id);
            return "redirect:/techcatalog";
        }

        @GetMapping("/techcatalog/edit/{id}")
        public String edit(@PathVariable Long id, Model model) {
            TechCatalog techcatalog = techCatalogService.getTechCatalogById(id);
            model.addAttribute("techcatalog", techcatalog);
            return "techcatalog/edit";
        }
    }