package com.web_ii.tech_catalog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
  
  @Controller
  public class TechCatalogController {
      @GetMapping("/techcatalog")
      public String getMethodName() {
          return "techcatalog/index";
      }
  }